@file:OptIn(ExperimentalMaterial3Api::class)

package net.wetheGoverned.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.wetheGoverned.model.CandidateManifesto
import net.wetheGoverned.model.PollScope

@Composable
fun ManifestoListScreen(
    onBack: () -> Unit,
    onManifestoClick: (String) -> Unit,
    viewModel: ManifestoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Candidate Manifestos") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Manifesto")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = if (uiState.selectedScope == PollScope.DISTRICT) 0 else 1) {
                Tab(
                    selected = uiState.selectedScope == PollScope.DISTRICT,
                    onClick = { viewModel.setScope(PollScope.DISTRICT) },
                    text = { Text("District") }
                )
                Tab(
                    selected = uiState.selectedScope == PollScope.LOCAL,
                    onClick = { viewModel.setScope(PollScope.LOCAL) },
                    text = { Text("Local") }
                )
            }

            val manifestos = if (uiState.selectedScope == PollScope.DISTRICT) 
                uiState.districtManifestos else uiState.localManifestos

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (manifestos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No manifestos for this scope yet.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(manifestos, key = { it.id }) { manifesto ->
                        Card(onClick = { onManifestoClick(manifesto.id) }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(manifesto.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    manifesto.body.replace("#district", "").replace("#local", "").take(160) + "…",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateManifestoDialog(
            userActivity = uiState.userPollActivity,
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, name, body, scope ->
                viewModel.publishManifesto(title, name, body, scope)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CreateManifestoDialog(
    userActivity: List<UserPollActivity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, PollScope) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf(PollScope.DISTRICT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Candidate Application & Manifesto") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    OutlinedTextField(
                        value = title, 
                        onValueChange = { title = it }, 
                        label = { Text("Title (e.g. City Council Candidate)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = name, 
                        onValueChange = { name = it }, 
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = body, 
                        onValueChange = { body = it }, 
                        label = { Text("Manifesto Body") }, 
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                }
                item {
                    Text("Target Scope:", style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = scope == PollScope.DISTRICT, onClick = { scope = PollScope.DISTRICT })
                        Text("District")
                        Spacer(Modifier.width(16.dp))
                        RadioButton(selected = scope == PollScope.LOCAL, onClick = { scope = PollScope.LOCAL })
                        Text("Local")
                    }
                }
                
                item {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("Civic Track Record (Automated)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("This activity will be appended to your manifesto for transparency.", style = MaterialTheme.typography.labelSmall)
                }
                
                if (userActivity.isEmpty()) {
                    item { Text("No poll activity found.", style = MaterialTheme.typography.bodySmall) }
                } else {
                    items(userActivity) { activity ->
                        Column(Modifier.padding(vertical = 4.dp)) {
                            Text(activity.pollQuestion, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("Voted: ${activity.votedOption ?: "Skipped"}", style = MaterialTheme.typography.bodySmall)
                            activity.discussionSnippet?.let {
                                Text("Comment: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, name, body, scope) }, 
                enabled = title.isNotBlank() && name.isNotBlank() && body.isNotBlank()
            ) {
                Text("Publish & Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ManifestoDetailScreen(
    manifestoId: String,
    onBack: () -> Unit,
    viewModel: ManifestoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val manifesto = uiState.manifestos.firstOrNull { it.id == manifestoId }

    LaunchedEffect(manifestoId) { viewModel.loadManifesto(manifestoId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manifesto Details") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } },
            )
        },
    ) { padding ->
        if (manifesto == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(16.dp)) {
                item {
                    Text(manifesto.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text(manifesto.body.replace("#district", "").replace("#local", ""), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
