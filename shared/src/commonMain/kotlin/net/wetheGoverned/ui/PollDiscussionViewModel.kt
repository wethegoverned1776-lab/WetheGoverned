package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.PollPost
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.session.SessionManager

data class DiscussionUiState(
    val posts: List<PollPost> = emptyList(),
    val optionLabel: String = "",
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false
)

open class PollDiscussionViewModel(
    private val repository: PollRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscussionUiState())
    val uiState: StateFlow<DiscussionUiState> = _uiState.asStateFlow()

    private var pollId: String = ""
    private var optionId: String = ""

    fun init(pollId: String, optionId: String) {
        this.pollId = pollId
        this.optionId = optionId
        
        viewModelScope.launch {
            val poll = repository.getPoll(pollId).getOrNull()
            val label = poll?.options?.find { it.id == optionId }?.label ?: ""
            _uiState.update { it.copy(optionLabel = label) }
        }

        repository.observeOptionPosts(pollId, optionId)
            .onEach { posts ->
                _uiState.update { it.copy(posts = posts) }
            }
            .launchIn(viewModelScope)
    }

    fun setShowAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show) }
    }

    fun createPost(headline: String, content: String) {
        if (headline.isBlank() || content.isBlank()) return
        val pubKey = sessionManager.currentPubKey ?: return
        if (pubKey == "guest_observer_hex") return
        
        val author = pubKey.take(12)
        viewModelScope.launch {
            repository.createPost(pollId, optionId, author, content, headline = headline)
            setShowAddDialog(false)
        }
    }

    fun vote(postId: String, delta: Int) {
        if (sessionManager.currentPubKey == "guest_observer_hex") return
        viewModelScope.launch {
            repository.voteOnPost(postId, delta)
        }
    }
}
