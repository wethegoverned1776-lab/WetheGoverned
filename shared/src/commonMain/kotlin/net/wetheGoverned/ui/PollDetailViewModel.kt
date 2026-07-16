package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager

data class PollDetailUiState(
    val poll: CivicPoll? = null,
    val discussions: List<PollPost> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val pendingSelection: String? = null,
    val canVote: Boolean = false,
    val isReadOnly: Boolean = false,
    val error: String? = null,
)

open class PollDetailViewModel(
    private val pollRepository: PollRepository,
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PollDetailUiState())
    val uiState: StateFlow<PollDetailUiState> = _uiState.asStateFlow()

    fun load(pollId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            pollRepository.getPoll(pollId)
                .onSuccess { poll ->
                    val isOther = sessionManager.currentSession?.districtId != poll.districtId
                    _uiState.update { it.copy(poll = poll, isReadOnly = isOther) }
                    
                    pollRepository.observePollPosts(pollId)
                        .onEach { posts ->
                            _uiState.update { it.copy(discussions = posts, isLoading = false) }
                        }
                        .launchIn(viewModelScope)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }

            val pubKey = sessionManager.currentPubKey
            if (pubKey != null) {
                residentRepository.getProfile(pubKey)
                    .onSuccess { profile ->
                        _uiState.update {
                            it.copy(canVote = profile.tier == VerificationTier.VERIFIED)
                        }
                    }
            }
        }
    }

    fun onSelectOption(optionId: String) {
        _uiState.update { it.copy(pendingSelection = optionId) }
    }

    fun onSubmitVote() {
        val pubKey = sessionManager.currentPubKey ?: return
        if (_uiState.value.isReadOnly || pubKey == "guest_observer_hex" || !_uiState.value.canVote) return
        
        val poll = _uiState.value.poll ?: return
        val option = _uiState.value.pendingSelection ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            pollRepository.vote(poll.id, option, pubKey)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false, pendingSelection = null) }
                    // Reload poll to show updated results
                    pollRepository.getPoll(poll.id).onSuccess { updatedPoll ->
                        _uiState.update { it.copy(poll = updatedPoll) }
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isSubmitting = false, error = "Vote failed: ${e.message}")
                    }
                }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}
