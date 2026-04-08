package net.wetheGoverned.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.ResidentProfile
import net.wetheGoverned.repository.ResidentRepository
import javax.inject.Inject

data class ResidentProfileUiState(
    val profile: ResidentProfile? = null,
    val isLoading: Boolean = true,
    val isOwnProfile: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ResidentProfileViewModel @Inject constructor(
    private val residentRepository: ResidentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResidentProfileUiState())
    val uiState: StateFlow<ResidentProfileUiState> = _uiState.asStateFlow()

    fun load(pubKey: String) {
        // Observe cache
        residentRepository
            .observeProfile(pubKey)
            .onEach { profile ->
                _uiState.update {
                    it.copy(
                        profile = profile,
                        isLoading = profile == null,
                        isOwnProfile = pubKey == currentUserPubKey(),
                    )
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

    fun dismissError() = _uiState.update { it.copy(error = null) }

    private fun currentUserPubKey(): String? = null // TODO: inject session
}
