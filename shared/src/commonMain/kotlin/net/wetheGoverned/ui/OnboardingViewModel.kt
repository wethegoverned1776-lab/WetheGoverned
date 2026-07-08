package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.core.Bech32Codec
import net.wetheGoverned.core.Secp256k1KeyManager
import net.wetheGoverned.model.*
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.remote.api.WtgBackendApi
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.ui.location.LocationHelper

enum class OnboardingStep { WELCOME, KEY_SETUP, COMPLETE }

data class OnboardingUiState(
    val step: OnboardingStep         = OnboardingStep.WELCOME,
    val isLoading: Boolean           = false,
    val generatedPubKeyHex: String?  = null,
    val privateKeyHex: String?       = null,
    val nsecDisplay: String?         = null,
    val error: String?               = null,
)

open class OnboardingViewModel(
    private val civicApi: CivicApi,
    private val backendApi: WtgBackendApi,
    private val sessionManager: SessionManager,
    private val locationHelper: LocationHelper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        refreshStep()
    }

    fun refreshStep() {
        val session = sessionManager.currentSession
        if (session != null) {
            _uiState.update { it.copy(step = OnboardingStep.COMPLETE) }
        } else {
            _uiState.update { it.copy(step = OnboardingStep.WELCOME) }
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    fun onWelcomeContinue()  = _uiState.update { it.copy(step = OnboardingStep.KEY_SETUP) }
    fun onKeySetupContinue() = _uiState.update { it.copy(step = OnboardingStep.COMPLETE) }

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

    // ── Completion ────────────────────────────────────────────────────────────

    fun completeOnboarding() {
        val state   = _uiState.value
        
        val currentSession = sessionManager.currentSession
        if (currentSession == null) {
            val pubKey  = state.generatedPubKeyHex ?: return
            val privKey = state.privateKeyHex ?: return

            sessionManager.login(
                pubKeyHex     = pubKey,
                privateKeyHex = privKey,
                districtId    = null,
                tier          = VerificationTier.UNVERIFIED,
                displayName   = pubKey.take(8),
            )
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun signOut() {
        sessionManager.logout()
    }

    fun reset() {
        _uiState.update { OnboardingUiState() }
    }
}
