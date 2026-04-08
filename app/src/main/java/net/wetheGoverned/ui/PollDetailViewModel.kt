package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.DistrictPoll
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.repository.ResidentRepository
import javax.inject.Inject

data class PollDetailUiState(
    val poll: DistrictPoll? = null,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val pendingSelection: String? = null,
    val canVote: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class PollDetailViewModel @Inject constructor(
    private val pollRepository: PollRepository,
    private val residentRepository: ResidentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PollDetailUiState())
    val uiState: StateFlow<PollDetailUiState> = _uiState.asStateFlow()

    fun load(pollId: String) {
        pollRepository
            .observeDistrictPolls(districtId = "us-fl-06")
            .map { polls -> polls.firstOrNull { it.id == pollId } }
            .onEach { poll ->
                _uiState.update { it.copy(poll = poll, isLoading = poll == null) }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            pollRepository.getPoll(pollId)
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }

        viewModelScope.launch {
            val pubKey = currentUserPubKey() ?: return@launch
            residentRepository.getProfile(pubKey)
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(canVote = profile.tier >= VerificationTier.TIER_2)
                    }
                }
        }
    }

    fun onSelectOption(optionId: String) {
        _uiState.update { it.copy(pendingSelection = optionId) }
    }

    fun onSubmitVote() {
        val poll = _uiState.value.poll ?: return
        val option = _uiState.value.pendingSelection ?: return
        val pubKey = currentUserPubKey() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            pollRepository.vote(poll.id, option, pubKey)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false, pendingSelection = null) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isSubmitting = false, error = "Vote failed: ${e.message}")
                    }
                }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    private fun currentUserPubKey(): String? = null // TODO: inject session manager
}

private operator fun VerificationTier.compareTo(other: VerificationTier): Int =
    this.ordinal.compareTo(other.ordinal)
