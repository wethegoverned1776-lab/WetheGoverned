package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.DistrictPoll
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pollRepository: PollRepository,
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observePolls()
    }

    private fun observePolls() {
        val districtId = _uiState.value.districtId
        pollRepository
            .observeDistrictPolls(districtId)
            .onEach { polls ->
                _uiState.update { it.copy(polls = polls, isLoading = false, error = null) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun onVote(pollId: String, optionId: String) {
        val pubKey = sessionManager.currentPubKey ?: return
        viewModelScope.launch {
            pollRepository.vote(pollId, optionId, pubKey)
                .onFailure { e ->
                    _uiState.update { it.copy(error = "Vote failed: ${e.message}") }
                }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}

data class HomeUiState(
    val districtId: String = "us-fl-06",
    val districtDisplayName: String = "Florida District 6",
    val polls: List<DistrictPoll> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
