package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager
import javax.inject.Inject

data class PollDetailUiState(
    val poll: CivicPoll? = null,
    val discussions: List<PollPost> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val pendingSelection: String? = null,
    val canVote: Boolean = true, 
    val isReadOnly: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class PollDetailViewModel @Inject constructor(
    private val pollRepository: PollRepository,
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PollDetailUiState())
    val uiState: StateFlow<PollDetailUiState> = _uiState.asStateFlow()

    fun load(pollId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // 1. Fetch Poll
            pollRepository.getPoll(pollId)
                .onSuccess { poll ->
                    val isOther = sessionManager.currentSession?.districtId != poll.districtId
                    _uiState.update { it.copy(poll = poll, isReadOnly = isOther) }
                    
                    // 2. Observe Discussions
                    pollRepository.observePollPosts(pollId)
                        .onEach { posts ->
                            _uiState.update { it.copy(discussions = posts, isLoading = false) }
                        }
                        .launchIn(viewModelScope)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }

            // 3. Check Voting Eligibility
            val pubKey = currentUserPubKey()
            if (pubKey != null) {
                residentRepository.getProfile(pubKey)
                    .onSuccess { profile ->
                        _uiState.update {
                            it.copy(canVote = profile.tier >= VerificationTier.TIER_2)
                        }
                    }
            }
        }
    }

    fun onSelectOption(optionId: String) {
        _uiState.update { it.copy(pendingSelection = optionId) }
    }

    fun onSubmitVote() {
        if (_uiState.value.isReadOnly) return
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

    private fun currentUserPubKey(): String? = sessionManager.currentPubKey
}

private operator fun VerificationTier.compareTo(other: VerificationTier): Int =
    this.ordinal.compareTo(other.ordinal)
