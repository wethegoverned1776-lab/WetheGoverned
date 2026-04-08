package net.wetheGoverned.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.core.Bech32Codec
import net.wetheGoverned.core.Secp256k1KeyManager
import net.wetheGoverned.model.District
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.remote.backend.WtgBackendApi
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.ui.location.LocationHelper
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// OnboardingViewModel – final
// LocationHelper injected; no hardcoded coordinates anywhere.
// ─────────────────────────────────────────────────────────────────────────────

enum class OnboardingStep { WELCOME, KEY_SETUP, DISTRICT_DETECT, EMAIL_VERIFY, COMPLETE }

data class OnboardingUiState(
    val step: OnboardingStep         = OnboardingStep.WELCOME,
    val isLoading: Boolean           = false,
    val detectedDistrict: District?  = null,
    val confirmedDistrictId: String  = "",
    val manualDistrictId: String     = "",
    val generatedPubKeyHex: String?  = null,
    val privateKeyHex: String?       = null,
    val nsecDisplay: String?         = null,
    val email: String                = "",
    val emailSent: Boolean           = false,
    val locationPermissionDenied: Boolean = false,
    val error: String?               = null,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val civicApi: CivicApi,
    private val backendApi: WtgBackendApi,
    private val sessionManager: SessionManager,
    private val locationHelper: LocationHelper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // ── Navigation ────────────────────────────────────────────────────────────

    fun onWelcomeContinue()  = _uiState.update { it.copy(step = OnboardingStep.KEY_SETUP) }
    fun onKeySetupContinue() = _uiState.update { it.copy(step = OnboardingStep.DISTRICT_DETECT) }

    // ── Key generation ────────────────────────────────────────────────────────

    fun generateNewKey() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val keyPair  = Secp256k1KeyManager.generateKeyPair()
                val nsecBech = Bech32Codec.encodeNsec(keyPair.privateKeyHex)
                _uiState.update {
                    it.copy(
                        isLoading          = false,
                        generatedPubKeyHex = keyPair.pubKeyHex,
                        privateKeyHex      = keyPair.privateKeyHex,
                        nsecDisplay        = nsecBech,
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = "Key generation failed: ${e.message}") }
            }
        }
    }

    fun importExistingKey(input: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val trimmed    = input.trim()
                val privKeyHex = when {
                    trimmed.startsWith("nsec1") -> Bech32Codec.decodeNsec(trimmed)
                    trimmed.length == 64        -> trimmed.lowercase()
                    else -> error("Provide an nsec1… key or a 64-character hex private key.")
                }
                val pubKeyHex = Secp256k1KeyManager.deriveXOnlyPubKey(privKeyHex)
                val nsecBech  = Bech32Codec.encodeNsec(privKeyHex)
                _uiState.update {
                    it.copy(
                        isLoading          = false,
                        generatedPubKeyHex = pubKeyHex,
                        privateKeyHex      = privKeyHex,
                        nsecDisplay        = nsecBech,
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // ── District detection via real GPS ───────────────────────────────────────

    /**
     * Called from the UI after the user grants ACCESS_COARSE_LOCATION.
     * Uses FusedLocationProviderClient via LocationHelper.
     */
    fun detectDistrictFromGps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val location = locationHelper.getCurrentLocation()
            if (location == null) {
                // Try last-known as fallback
                val last = locationHelper.getLastKnownLocation()
                if (last == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error     = "Could not get location. Please enter your district manually.",
                        )
                    }
                    return@launch
                }
                detectDistrictFromCoords(last.latitude, last.longitude)
            } else {
                detectDistrictFromCoords(location.latitude, location.longitude)
            }
        }
    }

    private suspend fun detectDistrictFromCoords(lat: Double, lng: Double) {
        runCatching { civicApi.detectDistrict(lat, lng) }
            .onSuccess { district ->
                _uiState.update {
                    it.copy(
                        isLoading           = false,
                        detectedDistrict    = district,
                        confirmedDistrictId = district.id,
                    )
                }
            }
            .onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error     = "Could not detect district: ${e.message}. Please enter manually.",
                    )
                }
            }
    }

    fun onLocationPermissionDenied() {
        _uiState.update { it.copy(locationPermissionDenied = true) }
    }

    // ── Manual district entry ─────────────────────────────────────────────────

    fun onManualDistrictChange(id: String) =
        _uiState.update { it.copy(manualDistrictId = id, error = null) }

    fun confirmDistrict() {
        val id = (_uiState.value.detectedDistrict?.id
            ?: _uiState.value.manualDistrictId).trim()
        if (id.isBlank()) {
            _uiState.update { it.copy(error = "Enter your district ID (e.g. us-wa-07)") }
            return
        }
        if (!id.matches(Regex("us-[a-z]{2}-\\d{2}"))) {
            _uiState.update { it.copy(error = "Format must be us-[state]-[number], e.g. us-wa-07") }
            return
        }
        _uiState.update {
            it.copy(confirmedDistrictId = id, step = OnboardingStep.EMAIL_VERIFY, error = null)
        }
    }

    // ── Email verification ────────────────────────────────────────────────────

    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email, error = null) }

    fun sendVerificationEmail() {
        val state  = _uiState.value
        val pubKey = state.generatedPubKeyHex ?: run {
            _uiState.update { it.copy(error = "Set up your key first") }; return
        }
        val email = state.email.trim()
        if (!email.contains('@') || !email.contains('.')) {
            _uiState.update { it.copy(error = "Enter a valid email address") }
            return
        }
        val districtId = state.confirmedDistrictId.ifBlank { "us-wa-07" }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { backendApi.sendVerificationEmail(pubKey, email, districtId) }
                .onSuccess { response ->
                    if (response.success) {
                        _uiState.update { it.copy(isLoading = false, emailSent = true) }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false,
                                error = response.message.ifBlank { "Email send failed. Try again." })
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false,
                            error = "Could not send email: ${e.message}")
                    }
                }
        }
    }

    fun skipEmailVerification() =
        _uiState.update { it.copy(step = OnboardingStep.COMPLETE) }

    // ── Completion ────────────────────────────────────────────────────────────

    fun completeOnboarding() {
        val state   = _uiState.value
        val pubKey  = state.generatedPubKeyHex ?: return
        val privKey = state.privateKeyHex ?: return
        val districtId  = state.confirmedDistrictId.ifBlank { "us-wa-07" }
        val displayName = state.email.substringBefore('@')
            .replaceFirstChar { it.uppercase() }.ifBlank { pubKey.take(8) }

        sessionManager.login(
            pubKeyHex     = pubKey,
            privateKeyHex = privKey,
            districtId    = districtId,
            tier          = if (state.emailSent) VerificationTier.TIER_1
                            else VerificationTier.UNVERIFIED,
            displayName   = displayName,
        )
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}
