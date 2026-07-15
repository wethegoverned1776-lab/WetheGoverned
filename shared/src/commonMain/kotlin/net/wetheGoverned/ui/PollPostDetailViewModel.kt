package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.PollPost
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.session.SessionManager

data class PostDetailUiState(
    val post: PollPost? = null,
    val replies: List<PollPost> = emptyList(),
    val isLoading: Boolean = false
)

open class PollPostDetailViewModel(
    private val repository: PollRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun load(postId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val post = repository.getPost(postId).getOrNull()
            _uiState.update { it.copy(post = post, isLoading = false) }
        }

        repository.observeThreadedPosts(postId)
            .onEach { replies ->
                _uiState.update { it.copy(replies = replies) }
            }
            .launchIn(viewModelScope)
    }

    fun submitReply(content: String) {
        val parentPost = _uiState.value.post ?: return
        if (content.isBlank()) return
        val session = sessionManager.currentSession ?: return
        if (session.tier != net.wetheGoverned.model.VerificationTier.VERIFIED) return
        
        val author = session.pubKey.take(12)
        
        viewModelScope.launch {
            repository.createPost(
                pollId = parentPost.pollId,
                optionId = parentPost.optionId,
                authorName = author,
                content = content,
                parentPostId = parentPost.id
            )
        }
    }

    fun voteOnReply(postId: String, delta: Int) {
        if (sessionManager.currentSession?.tier != net.wetheGoverned.model.VerificationTier.VERIFIED) return
        viewModelScope.launch {
            repository.voteOnPost(postId, delta)
        }
    }
}
