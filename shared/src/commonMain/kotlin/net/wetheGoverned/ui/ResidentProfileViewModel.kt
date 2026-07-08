package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.ResidentProfile
import net.wetheGoverned.repository.AccountRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager

data class ResidentProfileUiState(
    val profile: ResidentProfile? = null,
    val isLoading: Boolean = true,
    val isOwnProfile: Boolean = false,
    val registeredUsers: List<ResidentProfile> = emptyList(),
    val error: String? = null,
)

open class ResidentProfileViewModel(
    private val residentRepository: ResidentRepository,
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResidentProfileUiState())
    val uiState: StateFlow<ResidentProfileUiState> = _uiState.asStateFlow()

    fun load(pubKey: String) {
        // Observe cache
        residentRepository
            .observeProfile(pubKey)
            .onEach { profile ->
                val isGuest = pubKey == "guest_observer_hex"
                _uiState.update {
                    it.copy(
                        profile = profile,
                        isLoading = profile == null && !isGuest,
                        isOwnProfile = pubKey == sessionManager.currentPubKey && !isGuest,
                    )
                }
                
                // If it's the user's own profile, fetch users they verified
                if (pubKey == sessionManager.currentPubKey) {
                    loadVerifiedByMe(pubKey)
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)

        // Fetch from remote
        viewModelScope.launch {
            residentRepository.getProfile(pubKey)
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun loadVerifiedByMe(myPubKey: String) {
        residentRepository
            .observeProfilesVerifiedBy(myPubKey)
            .onEach { users ->
                _uiState.update { it.copy(registeredUsers = users) }
            }
            .launchIn(viewModelScope)
    }

    fun onUpdateDistrict(districtId: String) {
        val pubKey = sessionManager.currentPubKey ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // 1. Update Resident Identity
            val res1 = residentRepository.updateDistrict(pubKey, districtId)
            
            // 2. Update Account Credentials
            accountRepository.updateDistrict(pubKey, districtId)
            
            // 3. Update active session
            sessionManager.setDistrict(districtId)

            if (res1.isFailure) {
                _uiState.update { it.copy(isLoading = false, error = res1.exceptionOrNull()?.message) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun logout() {
        sessionManager.logout()
    }
}
