package net.wetheGoverned.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.wetheGoverned.model.DistrictPoll
import net.wetheGoverned.model.PollOption
import net.wetheGoverned.model.PollStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollDetailScreen(
    pollId: String,
    onBack: () -> Unit,
    viewModel: PollDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pollId) { viewModel.load(pollId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("District Poll") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            uiState.poll != null -> PollDetailContent(
                poll = uiState.poll!!,
                canVote = uiState.canVote,
                selectedOption = uiState.pendingSelection,
                onSelectOption = viewModel::onSelectOption,
                onSubmitVote = viewModel::onSubmitVote,
                isSubmitting = uiState.isSubmitting,
                modifier = Modifier.padding(padding),
            )

            else -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { Text("Poll not found.") }
        }
    }
}

@Composable
private fun PollDetailContent(
    poll: DistrictPoll,
    canVote: Boolean,
    selectedOption: String?,
    onSelectOption: (String) -> Unit,
    onSubmitVote: () -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(poll.question, style = MaterialTheme.typography.headlineSmall)
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadge(poll.status)
                Text(
                    "${poll.totalVotes} votes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                poll.closesAt?.let { CloseTimeBadge(it) }
            }
        }

        items(poll.options, key = { it.id }) { option ->
            PollOptionRow(
                option = option,
                isVoted = option.id == poll.residentVoteOption,
                isSelected = option.id == selectedOption,
                showResults = poll.residentVoteOption != null || poll.status == PollStatus.CLOSED,
                canVote = canVote && poll.status == PollStatus.ACTIVE,
                onSelect = { onSelectOption(option.id) },
            )
        }

        if (canVote && poll.status == PollStatus.ACTIVE && poll.residentVoteOption == null) {
            item {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = onSubmitVote,
                    enabled = selectedOption != null && !isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (isSubmitting) "Submitting…" else "Cast Vote")
                }
            }
        }

        if (!canVote && poll.status == PollStatus.ACTIVE) {
            item {
                TierGateCard()
            }
        }
    }
}

@Composable
private fun PollOptionRow(
    option: PollOption,
    isVoted: Boolean,
    isSelected: Boolean,
    showResults: Boolean,
    canVote: Boolean,
    onSelect: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (canVote) Modifier.selectable(
                    selected = isSelected,
                    onClick = onSelect,
                    role = Role.RadioButton,
                ) else Modifier,
            ),
        border = if (isSelected || isVoted)
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
            ) else null,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isVoted    -> MaterialTheme.colorScheme.primaryContainer
                isSelected -> MaterialTheme.colorScheme.secondaryContainer
                else       -> MaterialTheme.colorScheme.surface
            },
        ),
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (canVote && !showResults) {
                        RadioButton(selected = isSelected, onClick = null)
                    }
                    Text(
                        text = if (isVoted) "✓ ${option.label}" else option.label,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (showResults) {
                    Text(
                        "${(option.percentageOfTotal * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            if (showResults) {
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { option.percentageOfTotal },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isVoted) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary,
                )
                Text(
                    "${option.voteCount} votes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: PollStatus) {
    val (label, color) = when (status) {
        PollStatus.ACTIVE   -> "🟢 LIVE" to MaterialTheme.colorScheme.primary
        PollStatus.CLOSED   -> "⛔ Closed" to MaterialTheme.colorScheme.error
        PollStatus.ARCHIVED -> "🗄 Archived" to MaterialTheme.colorScheme.outline
    }
    Text(label, style = MaterialTheme.typography.labelMedium, color = color)
}

@Composable
private fun CloseTimeBadge(closesAt: Long) {
    val remaining = ((closesAt - System.currentTimeMillis()) / 1000 / 60).coerceAtLeast(0)
    val text = when {
        remaining < 60   -> "Closes in ${remaining}m"
        remaining < 1440 -> "Closes in ${remaining / 60}h"
        else             -> "Closes in ${remaining / 1440}d"
    }
    Text(text, style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun TierGateCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Tier 2 verification required to vote",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Verify your district address in your profile to participate in polls.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}
