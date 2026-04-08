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
import net.wetheGoverned.model.CandidateManifesto
import net.wetheGoverned.model.ManifestoQuestion

@Composable
fun ManifestoListScreen(
    onBack: () -> Unit,
    onManifestoClick: (String) -> Unit,
    viewModel: ManifestoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Candidate Manifestos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            uiState.manifestos.isEmpty() -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { Text("No candidate manifestos yet for your district.") }

            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.manifestos, key = { it.id }) { manifesto ->
                    Card(onClick = { onManifestoClick(manifesto.id) }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(manifesto.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                manifesto.body.take(160) + if (manifesto.body.length > 160) "…" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
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
                title = { Text("Manifesto") },
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
                    Text(manifesto.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(12.dp))
                    Text(manifesto.body, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
