@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.wetheGoverned.model.*

@Composable
fun ScorecardScreen(
    onBack: () -> Unit,
    snackbar: SnackbarController,
    viewModel: ScorecardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showError(it); viewModel.dismissError() }
    }

    Scaffold(
        topBar = { WtgTopBar(title = "Representative Scorecard", onBack = onBack) },
    ) { padding ->
        PullRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh    = viewModel::onRefresh,
            modifier     = Modifier.padding(padding),
        ) {
            when {
                state.isLoading -> LoadingScreen()
                state.scorecard == null -> EmptyScreen(
                    icon     = Icons.Default.BarChart,
                    title    = "No scorecard available",
                    subtitle = "Your district's scorecard will appear here once data is synced.",
                )
                else -> ScorecardContent(state.scorecard!!)
            }
        }
    }
}

@Composable
private fun ScorecardContent(scorecard: RepresentativeScorecard) {
    LazyColumn(
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
                        Text(scorecard.party, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(scorecard.districtId.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item { OverallScoreCard(scorecard.overallScore) }

        item { Text("Category Breakdown", style = MaterialTheme.typography.titleMedium) }

        items(scorecard.categories) { cat -> CategoryCard(cat) }

        item {
            Text(
                "Last updated: ${
                    java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US)
                        .format(java.util.Date(scorecard.lastUpdated * 1000))
                }",
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
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Overall score: $score out of 100, grade $grade" },
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Overall Score", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Based on attendance, responsiveness & legislation",
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
                MetricPill("Official", cat.officialValue)
                cat.residentReportedValue?.let {
                    MetricPill("Residents", it, resident = true)
                }
            }
            if (cat.residentReportedValue != null &&
                cat.residentReportedValue != cat.officialValue) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "⚠ Discrepancy vs official data",
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
