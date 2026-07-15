package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.model.District
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.repository.AccountRepository
import net.wetheGoverned.util.AddressUtils

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

open class TierVerificationViewModel(
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
            
            val addressFingerprint = AddressUtils.generateFingerprint(state.streetAddress, state.city, state.zipCode)
            val fullAddress = "${state.streetAddress}, ${state.city}, ${state.zipCode}".lowercase().trim()
            
            val resolution = civicApi.resolveAddress(fullAddress)
            delay(1000)

            if (resolution.federalDistrict == null) {
                _uiState.update { it.copy(state = VerificationState.FAILED, error = "Could not resolve address to official districts.") }
                return@launch
            }

            _uiState.update { it.copy(
                detectedCongressional = resolution.federalDistrict.displayName,
                detectedStateSenate = resolution.stateUpperDistrict?.displayName ?: "Not Detected", 
                detectedPrecinct = resolution.stateLowerDistrict?.displayName ?: "Not Detected"
            ) }

            _uiState.update { it.copy(state = VerificationState.VERIFYING_ROLLS) }
            val effectiveDistrictId = userDistrictId ?: resolution.federalDistrict.id
            val isRegistered = civicApi.verifyVoterRolls(state.firstName, state.lastName, fullAddress, effectiveDistrictId)
            delay(1000)

            if (!isRegistered) {
                val peers = residentRepository.observeProfileByFingerprint(addressFingerprint).firstOrNull()
                if (peers != null) {
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
                _uiState.update { it.copy(state = VerificationState.FAILED, error = "Registration Not Found: No matching voter in ${resolution.federalDistrict.displayName} records.") }
                return@launch
            }

            val residentCount = residentRepository.getResidentCountAtAddress(addressFingerprint)
            if (residentCount >= 4) {
                _uiState.update { it.copy(state = VerificationState.FAILED, error = "Household limit reached (Maximum 4 verified accounts per address).") }
                return@launch
            }

            if (userDistrictId != null && !resolution.federalDistrict.id.equals(userDistrictId, ignoreCase = true)) {
                _uiState.update { it.copy(state = VerificationState.FAILED, error = "District Mismatch: This address is in ${resolution.federalDistrict.displayName}, but you registered for $userDistrictId.") }
                return@launch
            }

            completeVerification(pubKey, resolution.federalDistrict.id, addressFingerprint, null)
        }
    }

    private suspend fun completeVerification(pubKey: String, districtId: String, fingerprint: String, vouchedBy: String?) {
        if (sessionManager.currentSession?.districtId == null) {
            sessionManager.setDistrict(districtId)
            accountRepository.updateDistrict(pubKey, districtId)
        }

        residentRepository.upgradeTierFull(
            pubKey = pubKey,
            newTier = VerificationTier.VERIFIED,
            fingerprint = fingerprint,
            verifiedBy = vouchedBy
        ).onSuccess {
            sessionManager.upgradeTier(VerificationTier.VERIFIED)
            _uiState.update { it.copy(state = VerificationState.SUCCESS) }
        }.onFailure { e ->
            _uiState.update { it.copy(state = VerificationState.FAILED, error = "Finalization Error: ${e.message}") }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null, state = VerificationState.IDLE) }
}
