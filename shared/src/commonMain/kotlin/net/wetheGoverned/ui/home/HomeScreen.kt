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
import net.wetheGoverned.model.*
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
    onNavigateToVerification: () -> Unit,
    onCreatePoll: () -> Unit,
    onLogout: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WeTheGoverned") },
                actions = {
                    IconButton(onClick = onLogout) { Icon(Icons.Default.Logout, "Logout") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Welcome, ${uiState.username ?: "Resident"}", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Text("Your Districts: ${uiState.federalHouseId ?: "None"}", modifier = Modifier.padding(16.dp))
                }
                item {
                    Button(onClick = onCreatePoll, modifier = Modifier.padding(16.dp)) {
                        Text("Create New Poll")
                    }
                }
            }
        }
    }
}
