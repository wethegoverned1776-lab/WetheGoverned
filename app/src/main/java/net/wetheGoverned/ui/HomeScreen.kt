@file:OptIn(ExperimentalMaterial3Api::class)

package net.wetheGoverned.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.wetheGoverned.model.DistrictPoll
import net.wetheGoverned.model.PollStatus

@Composable
fun HomeScreen(
    onNavigateToPoll: (String) -> Unit,
    onNavigateToScorecard: () -> Unit,
    onNavigateToManifestos: () -> Unit,
    onNavigateToMetrics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.districtDisplayName) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("🗳️") },
                    label = { Text("Polls") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToScorecard,
                    icon = { Text("📊") },
                    label = { Text("Scorecard") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToManifestos,
                    icon = { Text("📋") },
                    label = { Text("Candidates") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMetrics,
                    icon = { Text("📈") },
                    label = { Text("Metrics") },
                )
            }
        },
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(16.dp)) {
                items(uiState.polls, key = { it.id }) { poll ->
                    Card(
                        onClick = { onNavigateToPoll(poll.id) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(text = poll.question, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text(text = "${poll.totalVotes} votes", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
