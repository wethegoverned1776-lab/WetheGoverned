package net.wetheGoverned.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.*
import net.wetheGoverned.ui.CommunityBoardViewModel
import net.wetheGoverned.ui.CommunityBoardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityBoardScreen(
    viewModel: CommunityBoardViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Hub") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Post")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Category Filter
            ScrollableTabRow(
                selectedTabIndex = getTabIndex(uiState.selectedKind),
                edgePadding = 16.dp,
                divider = {}
            ) {
                Tab(
                    selected = uiState.selectedKind == null,
                    onClick = { viewModel.setKindFilter(null) },
                    text = { Text("All") }
                )
                CommunityPostKind.values().forEach { kind ->
                    Tab(
                        selected = uiState.selectedKind == kind,
                        onClick = { viewModel.setKindFilter(kind) },
                        text = { Text(kind.name.lowercase().capitalize()) }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (!uiState.isLocal) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Icon(Icons.Default.Lock, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Community Hub Restricted",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "The Community Hub is only available to verified residents of this local area. Please verify your address in your profile to join the conversation.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (uiState.filteredPosts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No posts yet. Be the first to share something!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredPosts) { post ->
                        PostCard(post)
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePostDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { kind, title, desc, price, loc, contact ->
                viewModel.createPost(kind, title, desc, price, loc, contact)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun PostCard(post: CommunityPost) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.kind.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                post.price?.let {
                    Text(
                        text = "$${it}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.description, style = MaterialTheme.typography.bodyMedium)
            
            if (post.location != null || post.contactInfo != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                if (post.location != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = post.location, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (post.contactInfo != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = post.contactInfo, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePostDialog(
    onDismiss: () -> Unit,
    onCreate: (CommunityPostKind, String, String, Double?, String?, String?) -> Unit
) {
    var kind by remember { mutableStateOf(CommunityPostKind.GENERAL) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Post") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Kind Selection (Simplified)
                CommunityPostKind.values().forEach { k ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = kind == k, onClick = { kind = k })
                        Text(k.name)
                    }
                }
                
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                if (kind == CommunityPostKind.MARKETPLACE) {
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
                }
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
                OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact Info") })
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(kind, title, description, price.toDoubleOrNull(), location.ifBlank { null }, contact.ifBlank { null }) }) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun getTabIndex(kind: CommunityPostKind?): Int {
    return if (kind == null) 0 else kind.ordinal + 1
}

private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
