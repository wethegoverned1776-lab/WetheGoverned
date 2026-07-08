package net.wetheGoverned.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorecardScreen(
    onBack: () -> Unit,
    viewModel: ScorecardViewModel,
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { WtgTopBar(title = "Jurisdiction Performance", onBack = onBack) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = when(state.selectedScope) {
                    CivicScope.FEDERAL -> 0
                    CivicScope.STATE -> 1
                    CivicScope.DISTRICT -> 2
                    CivicScope.LOCAL -> 3
                },
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = state.selectedScope == CivicScope.FEDERAL,
                    onClick = { viewModel.setScope(CivicScope.FEDERAL) },
                    text = { Text("Federal") }
                )
                Tab(
                    selected = state.selectedScope == CivicScope.STATE,
                    onClick = { viewModel.setScope(CivicScope.STATE) },
                    text = { Text("State") }
                )
                Tab(
                    selected = state.selectedScope == CivicScope.DISTRICT,
                    onClick = { viewModel.setScope(CivicScope.DISTRICT) },
                    text = { Text("District") }
                )
                Tab(
                    selected = state.selectedScope == CivicScope.LOCAL,
                    onClick = { viewModel.setScope(CivicScope.LOCAL) },
                    text = { Text("Local") }
                )
            }

            PullRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh    = viewModel::onRefresh,
                modifier     = Modifier.weight(1f),
            ) {
                when {
                    state.isLoading -> LoadingScreen()
                    state.scorecard == null -> EmptyScreen(
                        icon     = Icons.Default.BarChart,
                        title    = "No representative found",
                        subtitle = "Performance data for ${state.selectedScope.name.lowercase()} is not available in your current area.",
                    )
                    else -> ScorecardContent(state.scorecard!!)
                }
            }
        }
    }
}

@Composable
private fun ScorecardContent(scorecard: RepresentativeScorecard) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Row(
                    Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape    = MaterialTheme.shapes.extraLarge,
                        color    = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                scorecard.name.firstOrNull()?.uppercase() ?: "R",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                    Column {
                        Text(scorecard.name,  style = MaterialTheme.typography.titleLarge)
                        Text(scorecard.party, style = MaterialTheme.typography.bodySmall)
                        Text(scorecard.districtId.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item { OverallScoreCard(scorecard.overallScore) }

        item { Text("Metric Comparison", style = MaterialTheme.typography.titleMedium) }

        items(scorecard.categories) { cat -> CategoryCard(cat) }

        item {
            Text(
                "Last updated: ${scorecard.lastUpdated}", // Simplified for KMP without SimpleDateFormat
                style    = MaterialTheme.typography.labelSmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun OverallScoreCard(score: Int) {
    val grade = gradeFor(score)
    val color = gradeColor(grade)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Accountability Score", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Alignment with resident polls & data",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(grade, style = MaterialTheme.typography.displaySmall, color = color)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier.fillMaxWidth(),
                color    = color,
            )
            Text(
                "$score / 100",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CategoryCard(cat: ScorecardCategory) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    cat.name,
                    style    = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )
                val grade = gradeFor(cat.score)
                Text(
                    "$grade  ${cat.score}/100",
                    style = MaterialTheme.typography.labelLarge,
                    color = gradeColor(grade),
                )
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { cat.score / 100f },
                modifier = Modifier.fillMaxWidth(),
                color    = gradeColor(gradeFor(cat.score)),
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricPill("Reported", cat.officialValue)
                cat.residentReportedValue?.let {
                    MetricPill("Verified", it, resident = true)
                }
            }
            if (cat.residentReportedValue != null &&
                cat.residentReportedValue != cat.officialValue) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "⚠ Data mismatch detected",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun MetricPill(label: String, value: String, resident: Boolean = false) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (resident) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            "$label: $value",
            style    = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

private fun gradeFor(score: Int) = when {
    score >= 90 -> "A"; score >= 80 -> "B"; score >= 70 -> "C"
    score >= 60 -> "D"; else -> "F"
}

@Composable
private fun gradeColor(grade: String): Color = when (grade) {
    "A" -> Color(0xFF2E7D32); "B" -> Color(0xFF558B2F); "C" -> Color(0xFFF9A825)
    "D" -> Color(0xFFE65100); else -> MaterialTheme.colorScheme.error
}
