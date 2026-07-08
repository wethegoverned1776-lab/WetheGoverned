package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.wetheGoverned.core.Secp256k1KeyManager
import net.wetheGoverned.model.UserAccount
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.repository.AccountRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val isRegistered: Boolean = false,
    val selectedDistrictId: String? = null,
    val selectedDistrictName: String = "No District Selected",
    val isPasswordChanged: Boolean = false,
)

open class AuthViewModel(
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
    private val residentRepository: ResidentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onDistrictSelected(id: String, name: String) {
        _uiState.update { it.copy(selectedDistrictId = id, selectedDistrictName = name) }
    }

    fun register(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Username and password are required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // ERR_V2_04 FIX: Move key generation to background
            val keyPair = withContext(kotlinx.coroutines.Dispatchers.Default) {
                Secp256k1KeyManager.generateKeyPair()
            }

            val result = accountRepository.register(
                UserAccount(
                    username = username,
                    password = password,
                    pubKey = keyPair.pubKeyHex,
                    privateKey = keyPair.privateKeyHex,
                    districtId = _uiState.value.selectedDistrictId
                )
            )
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                login(username, password)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = accountRepository.login(username, password)
            result.onSuccess { account ->
                sessionManager.login(
                    pubKeyHex = account.pubKey,
                    privateKeyHex = account.privateKey,
                    districtId = account.districtId,
                    tier = if (username == "admin") VerificationTier.TIER_3 else VerificationTier.TIER_1,
                    displayName = username
                )
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun changePassword(username: String, newPass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = accountRepository.changePassword(username, newPass)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isPasswordChanged = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            sessionManager.login(
                pubKeyHex = "guest_observer_hex", // Hardcoded identifier
                privateKeyHex = null,
                districtId = null, // No district
                tier = VerificationTier.UNVERIFIED,
                displayName = "Observer"
            )
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    fun reset() {
        _uiState.update { AuthUiState() }
    }
}
