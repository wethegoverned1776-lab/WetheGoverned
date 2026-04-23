package net.wetheGoverned.ui.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.model.District
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.data.repository.AccountRepository
import java.security.MessageDigest
import javax.inject.Inject

enum class VerificationState { IDLE, DETECTING_DISTRICTS, VERIFYING_ROLLS, SUCCESS, FAILED }

data class TierVerificationUiState(
    val state: VerificationState          = VerificationState.IDLE,
    val firstName: String                 = "",
    val lastName: String                  = "",
    val streetAddress: String             = "",
    val city: String                      = "",
    val zipCode: String                   = "",
    val detectedCongressional: String?    = null,
    val detectedStateSenate: String?      = null,
    val detectedPrecinct: String?         = null,
    val householdPeers: List<String>      = emptyList(),
    val error: String?                    = null,
)

@HiltViewModel
class TierVerificationViewModel @Inject constructor(
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager,
    private val accountRepository: AccountRepository,
    private val civicApi: CivicApi, 
) : ViewModel() {

    private val _uiState = MutableStateFlow(TierVerificationUiState())
    val uiState: StateFlow<TierVerificationUiState> = _uiState.asStateFlow()

    fun onFirstNameChange(v: String) = _uiState.update { it.copy(firstName = v, error = null) }
    fun onLastNameChange(v: String)  = _uiState.update { it.copy(lastName = v, error = null) }
    fun onStreetAddressChange(v: String) = _uiState.update { it.copy(streetAddress = v, error = null) }
    fun onCityChange(v: String)          = _uiState.update { it.copy(city = v, error = null) }
    fun onZipCodeChange(v: String)       = _uiState.update { it.copy(zipCode = v, error = null) }

    fun startVerificationFlow() {
        val state = _uiState.value
        val pubKey = sessionManager.currentPubKey ?: return
        val userDistrictId = sessionManager.currentSession?.districtId

        if (state.firstName.isBlank() || state.lastName.isBlank() || state.streetAddress.isBlank()) {
            _uiState.update { it.copy(error = "Please provide your full name and registered address.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(state = VerificationState.DETECTING_DISTRICTS, error = null, householdPeers = emptyList()) }
            val fullAddress = "${state.streetAddress}, ${state.city}, ${state.zipCode}".lowercase().trim()
            val addressFingerprint = hashAddress(fullAddress)
            
            // STEP 1: Resolve address to district (GIS Server)
            val district = civicApi.getDistrictFromAddress(fullAddress)
            delay(1000)

            if (district == null) {
                _uiState.update { it.copy(state = VerificationState.FAILED, error = "Could not resolve address to official districts.") }
                return@launch
            }

            _uiState.update { it.copy(
                detectedCongressional = district.displayName,
                detectedStateSenate = "${district.state} Senate District ${district.districtNumber + 2}", 
                detectedPrecinct = "Precinct ${district.districtNumber * 10}" 
            ) }

            // STEP 2: Voter Roll Verification (Option A - Voter Roll API)
            _uiState.update { it.copy(state = VerificationState.VERIFYING_ROLLS) }
            val effectiveDistrictId = userDistrictId ?: district.id
            val isRegistered = civicApi.verifyVoterRolls(state.firstName, state.lastName, fullAddress, effectiveDistrictId)
            delay(1000)

            if (!isRegistered) {
                // Check if anyone else at this address can vouch (Option B - P2P Notary)
                val peers = residentRepository.observeProfileByFingerprint(addressFingerprint).firstOrNull()
                if (peers != null) {
                    // Rule: Household vouching is limited to one other.
                    val vouchCount = residentRepository.getVouchCount(peers.pubKey)
                    if (vouchCount < 1) {
                        _uiState.update { it.copy(
                            state = VerificationState.IDLE, 
                            householdPeers = listOf(peers.pubKey), 
                            error = "Voter Roll match failed. However, a verified resident lives here and can vouch for you (Limit: 1 vouch per resident)."
                        ) }
                        return@launch
                    } else {
                        _uiState.update { it.copy(state = VerificationState.FAILED, error = "Registration Not Found. A resident at this address has already used their one-time vouch credit.") }
                        return@launch
                    }
                }
                _uiState.update { it.copy(state = VerificationState.FAILED, error = "Registration Not Found: No matching voter in ${district.displayName} records.") }
                return@launch
            }

            // Standard Household Limit (Security Cap)
            val residentCount = residentRepository.getResidentCountAtAddress(addressFingerprint)
            if (residentCount >= 4) {
                _uiState.update { it.copy(state = VerificationState.FAILED, error = "Household limit reached (Maximum 4 verified accounts per address).") }
                return@launch
            }

            // District Integrity Check
            if (userDistrictId != null && !district.id.equals(userDistrictId, ignoreCase = true)) {
                _uiState.update { it.copy(state = VerificationState.FAILED, error = "District Mismatch: This address is in ${district.displayName}, but you registered for $userDistrictId.") }
                return@launch
            }

            completeVerification(pubKey, district.id, state.firstName, state.lastName, addressFingerprint, null)
        }
    }

    fun submitP2PNotarization(notaryPubKey: String) {
        val state = _uiState.value
        val pubKey = sessionManager.currentPubKey ?: return
        val userDistrictId = sessionManager.currentSession?.districtId ?: "us-fl-06"

        viewModelScope.launch {
            _uiState.update { it.copy(state = VerificationState.VERIFYING_ROLLS, error = null) }
            val fullAddress = "${state.streetAddress}, ${state.city}, ${state.zipCode}".lowercase().trim()
            val addressFingerprint = hashAddress(fullAddress)
            
            delay(1000)
            completeVerification(pubKey, userDistrictId, state.firstName, state.lastName, addressFingerprint, notaryPubKey)
        }
    }

    private suspend fun completeVerification(pubKey: String, districtId: String, first: String, last: String, fingerprint: String, vouchedBy: String?) {
        if (sessionManager.currentSession?.districtId == null) {
            sessionManager.setDistrict(districtId)
            accountRepository.updateDistrict(pubKey, districtId)
        }

        residentRepository.upgradeTierFull(
            pubKey = pubKey,
            newTier = VerificationTier.TIER_2,
            firstName = first,
            lastName = last,
            fingerprint = fingerprint,
            verifiedBy = vouchedBy
        ).onSuccess {
            sessionManager.upgradeTier(VerificationTier.TIER_2)
            _uiState.update { it.copy(state = VerificationState.SUCCESS) }
        }.onFailure { e ->
            _uiState.update { it.copy(state = VerificationState.FAILED, error = "Finalization Error: ${e.message}") }
        }
    }

    private fun hashAddress(address: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(address.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun dismissError() = _uiState.update { it.copy(error = null, state = VerificationState.IDLE) }
}
