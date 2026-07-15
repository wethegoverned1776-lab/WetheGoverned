package net.wetheGoverned.ui

import androidx.compose.foundation.BorderStroke
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
import net.wetheGoverned.model.CivicScope
import net.wetheGoverned.model.PollScope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManifestoListScreen(
    onBack: () -> Unit,
    onManifestoClick: (String) -> Unit,
    viewModel: ManifestoViewModel
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
            ScrollableTabRow(
                selectedTabIndex = PollScope.entries.indexOf(uiState.selectedScope).coerceAtLeast(0),
                edgePadding = 16.dp
            ) {
                for (scope in PollScope.entries) {
                    Tab(
                        selected = uiState.selectedScope == scope,
                        onClick = { viewModel.setScope(scope) },
                        text = { Text(scope.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            val manifestos = uiState.filteredManifestos

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
                        OutlinedCard(
                            onClick = { onManifestoClick(manifesto.id) }, 
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(manifesto.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    manifesto.body.take(160) + "…",
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateManifestoDialog(
    userActivity: List<UserPollActivity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, CivicScope) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf(CivicScope.DISTRICT) }

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
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CivicScope.entries.forEach { s ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = scope == s, onClick = { scope = s })
                                Text(s.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManifestoDetailScreen(
    manifestoId: String,
    onBack: () -> Unit,
    viewModel: ManifestoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val manifesto = uiState.manifestos.find { it.id == manifestoId }

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
                    Text(manifesto.body, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
