package net.wetheGoverned.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.PollPost
import net.wetheGoverned.repository.PollRepository
import javax.inject.Inject

data class PostDetailUiState(
    val post: PollPost? = null,
    val replies: List<PollPost> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class PollPostDetailViewModel @Inject constructor(
    private val repository: PollRepository
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
        
        viewModelScope.launch {
            repository.createPost(
                pollId = parentPost.pollId,
                optionId = parentPost.optionId,
                authorName = "Resident",
                content = content,
                parentPostId = parentPost.id
            )
        }
    }

    fun voteOnReply(postId: String, delta: Int) {
        viewModelScope.launch {
            repository.voteOnPost(postId, delta)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollPostDetailScreen(
    postId: String,
    onBack: () -> Unit,
    viewModel: PollPostDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var replyText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.load(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Discussion") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Reply to this post...") }
                    )
                    IconButton(onClick = {
                        viewModel.submitReply(replyText)
                        replyText = ""
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.post?.let { post ->
                item {
                    Text(
                        text = post.headline ?: "Discussion",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(text = post.content, style = MaterialTheme.typography.bodyLarge)
                    Divider(Modifier.padding(vertical = 16.dp))
                    Text("Replies", style = MaterialTheme.typography.titleMedium)
                }
            }

            items(uiState.replies) { reply ->
                PostItem(
                    post = reply,
                    onVote = { delta -> viewModel.voteOnReply(reply.id, delta) },
                    onClick = { /* No deep threading for now */ }
                )
            }
        }
    }
}
