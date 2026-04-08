package net.wetheGoverned.ui.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.remote.backend.WtgBackendApi
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionEvent
import net.wetheGoverned.session.SessionManager
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// TierVerificationViewModel – v2
// Replaces delay() stubs with real WtgBackendApi calls.
// ─────────────────────────────────────────────────────────────────────────────

enum class VerificationState { IDLE, SUBMITTING, WAITING_CONFIRMATION, SUCCESS, FAILED }

data class TierVerificationUiState(
    val targetTier: VerificationTier      = VerificationTier.TIER_2,
    val currentTier: VerificationTier     = VerificationTier.TIER_1,
    val state: VerificationState          = VerificationState.IDLE,
    val streetAddress: String             = "",
    val city: String                      = "",
    val zipCode: String                   = "",
    val verificationSessionUrl: String?   = null,
    val identitySessionId: String?        = null,
    val error: String?                    = null,
)

@HiltViewModel
class TierVerificationViewModel @Inject constructor(
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager,
    private val backendApi: WtgBackendApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TierVerificationUiState())
    val uiState: StateFlow<TierVerificationUiState> = _uiState.asStateFlow()

    init {
        val session = sessionManager.currentSession
        if (session != null) {
            _uiState.update {
                it.copy(
                    currentTier = session.tier,
                    targetTier  = if (session.tier < VerificationTier.TIER_2)
                                      VerificationTier.TIER_2
                                  else VerificationTier.TIER_3,
                )
            }
        }
        // Listen for identity-verified deep link events from MainActivity
        viewModelScope.launch {
            sessionManager.events.collect { event ->
                if (event is SessionEvent.IdentityVerified) {
                    onIdentityVerified(event.proofToken)
                }
            }
        }
    }

    // ── Field updates ─────────────────────────────────────────────────────────

    fun onStreetAddressChange(v: String) = _uiState.update { it.copy(streetAddress = v, error = null) }
    fun onCityChange(v: String)          = _uiState.update { it.copy(city = v, error = null) }
    fun onZipCodeChange(v: String)       = _uiState.update { it.copy(zipCode = v, error = null) }

    // ── Tier 2: Address verification ──────────────────────────────────────────

    fun submitAddressVerification() {
        val state  = _uiState.value
        val pubKey = sessionManager.currentPubKey ?: run {
            _uiState.update { it.copy(error = "Not logged in") }; return
        }
        val districtId = sessionManager.currentSession?.districtId ?: run {
            _uiState.update { it.copy(error = "No district assigned") }; return
        }
        if (state.streetAddress.isBlank() || state.city.isBlank() || state.zipCode.isBlank()) {
            _uiState.update { it.copy(error = "All address fields are required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(state = VerificationState.SUBMITTING, error = null) }
            runCatching {
                // 1. Submit to WeTheGoverned backend → get proof token
                val proofResponse = backendApi.submitAddressVerification(
                    pubKey     = pubKey,
                    address    = state.streetAddress,
                    city       = state.city,
                    zipCode    = state.zipCode,
                    districtId = districtId,
                )
                // 2. Upgrade tier in Nostr profile + local Room cache
                residentRepository.upgradeTier(
                    pubKey     = pubKey,
                    newTier    = VerificationTier.TIER_2,
                    proofToken = proofResponse.proofToken,
                ).getOrThrow()
            }.onSuccess {
                sessionManager.upgradeTier(VerificationTier.TIER_2)
                _uiState.update { it.copy(state = VerificationState.SUCCESS) }
            }.onFailure { e ->
                val msg = when {
                    e.message?.contains("district") == true ->
                        "This address is not in your declared district. " +
                        "Please check your address or update your district."
                    e.message?.contains("timeout") == true ->
                        "Request timed out. Please check your connection and try again."
                    else -> "Verification failed: ${e.message}"
                }
                _uiState.update { it.copy(state = VerificationState.FAILED, error = msg) }
            }
        }
    }

    // ── Tier 3: Identity verification ─────────────────────────────────────────

    fun requestIdentityVerificationSession() {
        val pubKey     = sessionManager.currentPubKey ?: run {
            _uiState.update { it.copy(error = "Not logged in") }; return
        }
        val districtId = sessionManager.currentSession?.districtId ?: "us-fl-06"

        viewModelScope.launch {
            _uiState.update { it.copy(state = VerificationState.SUBMITTING, error = null) }
            runCatching {
                backendApi.createIdentitySession(pubKey, districtId)
            }.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        state                  = VerificationState.WAITING_CONFIRMATION,
                        verificationSessionUrl = response.sessionUrl,
                        identitySessionId      = response.sessionId,
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(state = VerificationState.IDLE,
                            error = "Could not start verification: ${e.message}")
                }
            }
        }
    }

    /** Called from the SessionEvent collector when wtg://identity-verified deep link fires. */
    private fun onIdentityVerified(proofOrSessionId: String) {
        val pubKey    = sessionManager.currentPubKey ?: return
        val sessionId = _uiState.value.identitySessionId ?: proofOrSessionId

        viewModelScope.launch {
            runCatching {
                val proofResponse = backendApi.confirmIdentitySession(pubKey, sessionId)
                residentRepository.upgradeTier(
                    pubKey     = pubKey,
                    newTier    = VerificationTier.TIER_3,
                    proofToken = proofResponse.proofToken,
                ).getOrThrow()
            }.onSuccess {
                sessionManager.upgradeTier(VerificationTier.TIER_3)
                _uiState.update { it.copy(state = VerificationState.SUCCESS) }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(state = VerificationState.FAILED,
                            error = "Identity confirmation failed: ${e.message}")
                }
            }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null, state = VerificationState.IDLE) }
    fun retry()        = _uiState.update { it.copy(state = VerificationState.IDLE, error = null) }
}

private operator fun VerificationTier.compareTo(other: VerificationTier): Int =
    this.ordinal.compareTo(other.ordinal)
