package net.wetheGoverned.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.CivicPoll
import net.wetheGoverned.model.PollScope
import net.wetheGoverned.ui.HomeViewModel
import net.wetheGoverned.ui.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToPoll: (String) -> Unit,
    onNavigateToDistrictSelection: () -> Unit,
    onNavigateToManifestos: () -> Unit,
    onNavigateToMetrics: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToCommunityHub: () -> Unit,
    onCreatePoll: () -> Unit,
    onLogout: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        onNavigateToPoll = onNavigateToPoll,
        onNavigateToDistrictSelection = onNavigateToDistrictSelection,
        onNavigateToManifestos = onNavigateToManifestos,
        onNavigateToMetrics = onNavigateToMetrics,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToCommunityHub = onNavigateToCommunityHub,
        onCreatePoll = onCreatePoll,
        onLogout = {
            viewModel.logout()
            onLogout()
        },
        onReturnToHomeDistrict = { viewModel.returnToHomeDistrict() },
        onScopeSelected = { viewModel.setScope(it) },
        onImportanceVote = { id, delta -> viewModel.onImportanceVote(id, delta) },
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onNavigateToPoll: (String) -> Unit,
    onNavigateToDistrictSelection: () -> Unit,
    onNavigateToManifestos: () -> Unit,
    onNavigateToMetrics: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToCommunityHub: () -> Unit,
    onCreatePoll: () -> Unit,
    onLogout: () -> Unit,
    onReturnToHomeDistrict: () -> Unit,
    onScopeSelected: (PollScope) -> Unit,
    onImportanceVote: (String, Int) -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    // Determine if we are in "Guest/Other District" mode
    val isReadOnly = uiState.isOtherDistrict

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isReadOnly) "${uiState.districtDisplayName} (READ-ONLY)" else uiState.districtDisplayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        uiState.localId?.let {
                            if (!isReadOnly) {
                                Text(
                                    text = "Local: $it",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                        if (uiState.isSyncing) {
                            Text(
                                "Live Sync Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    if (isReadOnly) {
                        TextButton(onClick = onReturnToHomeDistrict) {
                            Text("Back to Home")
                        }
                    }
                    IconButton(onClick = {
                        uiState.username?.let { onNavigateToProfile(it) }
                    }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isReadOnly) {
                FloatingActionButton(onClick = onCreatePoll) {
                    Icon(Icons.Default.Add, contentDescription = "Create Poll")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Scope Tabs
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedScope.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                PollScope.entries.forEach { scope ->
                    Tab(
                        selected = uiState.selectedScope == scope,
                        onClick = { onScopeSelected(scope) },
                        text = { Text(scope.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search polls or districts...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )

            if (uiState.isLoading && uiState.polls.isEmpty()) {
                LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
                    items(5) { PollSkeleton() }
                }
            } else if (uiState.groupedPolls.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No active polls in this scope.")
                }
            } else {
                PollFeed(
                    uiState = uiState,
                    isReadOnly = isReadOnly,
                    onImportanceVote = onImportanceVote,
                    onNavigateToPoll = onNavigateToPoll
                )
            }
        }
    }
}

@Composable
fun PollFeed(
    uiState: HomeUiState,
    isReadOnly: Boolean,
    onImportanceVote: (String, Int) -> Unit,
    onNavigateToPoll: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        uiState.groupedPolls.forEach { (groupName, polls) ->
            item {
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(polls, key = { it.id }) { poll ->
                PollImportanceCard(
                    poll = poll,
                    isReadOnly = isReadOnly,
                    onImportanceVote = { delta -> onImportanceVote(poll.id, delta) },
                    onClick = { onNavigateToPoll(poll.id) }
                )
            }
        }
    }
}

@Composable
fun PollSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Box(Modifier.fillMaxSize())
    }
}

@Composable
fun PollImportanceCard(
    poll: CivicPoll,
    isReadOnly: Boolean,
    onImportanceVote: (Int) -> Unit,
    onClick: () -> Unit
) {
    val hasVoted = poll.residentVoteOption != null

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasVoted) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else 
                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        border = if (hasVoted) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(48.dp)
            ) {
                IconButton(
                    onClick = { onImportanceVote(1) },
                    enabled = !isReadOnly
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        null,
                        tint = if (poll.userImportanceVote > 0) Color.Green else Color.Gray
                    )
                }
                Text(poll.importanceScore.toString(), fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { onImportanceVote(-1) },
                    enabled = !isReadOnly
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        null,
                        tint = if (poll.userImportanceVote < 0) Color.Red else Color.Gray
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = poll.question, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    if (hasVoted) {
                        Icon(
                            Icons.Default.CheckCircle, 
                            "Voted", 
                            tint = MaterialTheme.colorScheme.primary, 
                            modifier = Modifier.size(20.dp).padding(start = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${poll.totalVotes} votes", style = MaterialTheme.typography.bodySmall)
                    Surface(
                        color = when(poll.scope) {
                            PollScope.FEDERAL -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)
                            PollScope.STATE -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                            PollScope.DISTRICT -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                            PollScope.LOCAL -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                        },
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = poll.scope.name,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
