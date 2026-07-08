package net.wetheGoverned.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.PollPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollDiscussionScreen(
    pollId: String,
    optionId: String,
    viewModel: PollDiscussionViewModel,
    onBack: () -> Unit,
    onNavigateToPost: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pollId, optionId) {
        viewModel.init(pollId, optionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discussion: ${uiState.optionLabel}") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.setShowAddDialog(true) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Post")
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
            items(uiState.posts, key = { it.id }) { post ->
                PostItem(
                    post = post,
                    onVote = { delta -> viewModel.vote(post.id, delta) },
                    onClick = { onNavigateToPost(post.id) }
                )
            }
        }

        if (uiState.showAddDialog) {
            AddPostDialog(
                onDismiss = { viewModel.setShowAddDialog(false) },
                onConfirm = viewModel::createPost
            )
        }
    }
}

@Composable
fun PostItem(
    post: PollPost,
    onVote: (Int) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(48.dp)
            ) {
                IconButton(onClick = { onVote(1) }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Upvote",
                        tint = if (post.userVote == 1) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
                Text(
                    text = post.score.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        post.userVote == 1 -> MaterialTheme.colorScheme.primary
                        post.userVote == -1 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                IconButton(onClick = { onVote(-1) }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Downvote",
                        tint = if (post.userVote == -1) MaterialTheme.colorScheme.error else Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                post.headline?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "By ${post.authorName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var headline by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Post") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    label = { Text("Headline") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(headline, content) }) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
