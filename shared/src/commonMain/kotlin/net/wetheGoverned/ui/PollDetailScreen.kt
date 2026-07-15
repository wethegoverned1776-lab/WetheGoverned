package net.wetheGoverned.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollDetailScreen(
    pollId: String,
    viewModel: PollDetailViewModel,
    onBack: () -> Unit,
    onNavigateToDiscussion: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pollId) { viewModel.load(pollId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Poll Details") },
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
                onNavigateToDiscussion = { optionId -> onNavigateToDiscussion(pollId, optionId) },
                isSubmitting = uiState.isSubmitting,
                discussions = uiState.discussions,
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
    poll: CivicPoll,
    canVote: Boolean,
    selectedOption: String?,
    onSelectOption: (String) -> Unit,
    onSubmitVote: () -> Unit,
    onNavigateToDiscussion: (String) -> Unit,
    isSubmitting: Boolean,
    discussions: List<PollPost>,
    modifier: Modifier = Modifier,
) {
    val weightedResults = remember(poll) { poll.calculateWeightedResults() }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(poll.question, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        if (poll.scope == PollScope.STATE || poll.scope == PollScope.FEDERAL) {
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF7F9F9))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "⚖️ State Weighted Result", 
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Each district has equal voting power (1 vote). The result is the average support across all participating districts.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadge(poll.status)
                Text(
                    "${poll.totalVotes} total verified votes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(poll.options, key = { it.id }) { option ->
            val optionDiscussions = discussions.filter { it.optionId == option.id }
            val displayPercentage = if (poll.scope == net.wetheGoverned.model.CivicScope.STATE || poll.scope == net.wetheGoverned.model.CivicScope.FEDERAL) {
                weightedResults[option.id] ?: 0f
            } else {
                option.percentageOfTotal
            }

            PollOptionRow(
                option = option,
                isVoted = option.id == poll.residentVoteOption,
                isSelected = option.id == selectedOption,
                showResults = poll.residentVoteOption != null || poll.status == PollStatus.CLOSED,
                canVote = canVote && poll.status == PollStatus.ACTIVE,
                discussionCount = optionDiscussions.size,
                displayPercentage = displayPercentage,
                onSelect = { onSelectOption(option.id) },
                onDiscussionClick = { onNavigateToDiscussion(option.id) }
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

        if (selectedOption != null) {
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text(
                    text = "Discussions for: ${poll.options.find { it.id == selectedOption }?.label}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val filteredDiscussions = discussions.filter { it.optionId == selectedOption }
            if (filteredDiscussions.isEmpty()) {
                item {
                    Text("No discussions yet for this perspective.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            } else {
                items(filteredDiscussions) { post ->
                    DiscussionItem(post)
                }
            }
        }
    }
}

@Composable
fun DiscussionItem(post: PollPost) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(post.authorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            Text(post.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("Score: ${post.score}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
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
    discussionCount: Int,
    displayPercentage: Float,
    onSelect: () -> Unit,
    onDiscussionClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton,
            ),
        border = BorderStroke(
            width = if (isSelected || isVoted) 2.dp else 1.dp,
            color = if (isSelected || isVoted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = when {
                isVoted    -> Color(0xFFF7F9F9)
                isSelected -> Color(0xFFF7F9F9)
                else       -> Color.White
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
                    modifier = Modifier.weight(1f)
                ) {
                    RadioButton(selected = isSelected, onClick = null)
                    Text(
                        text = if (isVoted) "✓ ${option.label}" else option.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected || isVoted) FontWeight.Bold else FontWeight.Normal
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDiscussionClick) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    Text("$discussionCount")
                    if (showResults) {
                        Text(
                            "${(displayPercentage * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            if (showResults) {
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { displayPercentage },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isVoted) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary,
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
