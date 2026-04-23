package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.data.repository.AccountRepository
import net.wetheGoverned.local.dao.ResidentProfileDao
import net.wetheGoverned.local.entity.ResidentProfileEntity
import net.wetheGoverned.model.UserAccount
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.util.MnemonicUtils
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val isRegistered: Boolean = false,
    val generatedMnemonic: String? = null,
    val selectedDistrictId: String? = null,
    val selectedDistrictName: String = "No District Selected"
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
    private val profileDao: ResidentProfileDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onDistrictSelected(id: String, name: String) {
        _uiState.update { it.copy(selectedDistrictId = id, selectedDistrictName = name) }
    }

    fun generateMnemonic() {
        val mnemonic = MnemonicUtils.generateMnemonic()
        _uiState.update { it.copy(generatedMnemonic = mnemonic) }
    }

    fun register(username: String, password: String, displayName: String) {
        if (username.isBlank() || password.isBlank() || displayName.isBlank()) {
            _uiState.update { it.copy(error = "All fields are required") }
            return
        }

        val mnemonic = _uiState.value.generatedMnemonic ?: MnemonicUtils.generateMnemonic()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = accountRepository.register(
                UserAccount(
                    username = username,
                    password = password,
                    displayName = displayName,
                    districtId = _uiState.value.selectedDistrictId
                )
            )
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isRegistered = true, generatedMnemonic = mnemonic) }
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
                // Ensure a local profile exists in the DB for the app-server to manage
                val existingProfile = profileDao.getProfile(account.username)
                if (existingProfile == null) {
                    profileDao.upsertProfile(
                        ResidentProfileEntity(
                            pubKey = account.username,
                            displayName = account.displayName,
                            districtId = account.districtId,
                            localId = null,
                            tier = VerificationTier.UNVERIFIED.name,
                            avatarUrl = null,
                            bio = null,
                            joinedAt = System.currentTimeMillis(),
                            addressFingerprint = null,
                            cachedAt = System.currentTimeMillis()
                        )
                    )
                }

                sessionManager.login(
                    pubKeyHex = account.username,
                    privateKeyHex = "",
                    districtId = account.districtId,
                    tier = existingProfile?.let { VerificationTier.valueOf(it.tier) } ?: VerificationTier.UNVERIFIED,
                    displayName = account.displayName
                )
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun importAccount(mnemonic: String, password: String) {
        if (!MnemonicUtils.validateMnemonic(mnemonic)) {
            _uiState.update { it.copy(error = "Invalid 12-word recovery phrase") }
            return
        }
        _uiState.update { it.copy(error = "Mnemonic verified. Prototype import successful for demonstration.") }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
    fun clearMnemonic() = _uiState.update { it.copy(generatedMnemonic = null) }
}
