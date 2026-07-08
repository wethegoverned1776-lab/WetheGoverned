package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.CommunityRepository
import net.wetheGoverned.session.SessionManager

data class CommunityBoardUiState(
    val posts: List<CommunityPost> = emptyList(),
    val filteredPosts: List<CommunityPost> = emptyList(),
    val selectedKind: CommunityPostKind? = null,
    val isLoading: Boolean = false,
    val districtId: String? = null,
    val isLocal: Boolean = false,
    val error: String? = null
)

class CommunityBoardViewModel(
    private val communityRepository: CommunityRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityBoardUiState())
    val uiState: StateFlow<CommunityBoardUiState> = _uiState.asStateFlow()

    init {
        val session = sessionManager.currentSession
        val userDistrictId = session?.districtId
        _uiState.update { it.copy(districtId = userDistrictId, isLocal = userDistrictId != null) }
        
        if (userDistrictId != null) {
            observePosts(userDistrictId)
        }
    }

    private fun observePosts(districtId: String) {
        communityRepository.observePosts(districtId)
            .onStart { _uiState.update { it.copy(isLoading = true) } }
            .onEach { posts ->
                _uiState.update { state ->
                    state.copy(
                        posts = posts,
                        filteredPosts = if (state.selectedKind != null) posts.filter { it.kind == state.selectedKind } else posts,
                        isLoading = false
                    )
                }
            }
            .catch { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    fun setKindFilter(kind: CommunityPostKind?) {
        _uiState.update { state ->
            state.copy(
                selectedKind = kind,
                filteredPosts = if (kind != null) state.posts.filter { it.kind == kind } else state.posts
            )
        }
    }

    fun createPost(kind: CommunityPostKind, title: String, description: String, price: Double? = null, location: String? = null, contactInfo: String? = null) {
        val districtId = _uiState.value.districtId ?: return
        val pubKey = sessionManager.currentPubKey ?: return
        
        viewModelScope.launch {
            communityRepository.createPost(districtId, pubKey, kind, title, description, price, location, contactInfo)
                .onFailure { e -> _uiState.update { it.copy(error = "Failed to post: ${e.message}") } }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            communityRepository.deletePost(postId)
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}
