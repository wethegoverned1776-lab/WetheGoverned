package net.wetheGoverned.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.DistrictPoll

@Composable
fun PollItem(
    poll: DistrictPoll,
    onVote: (Int) -> Unit,   // +1 or -1
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = poll.question, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onVote(+1) }) {
                    Icon(Icons.Default.ThumbUp, contentDescription = "Upvote")
                }
                Text("${poll.options.sumOf { it.voteCount }}", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { onVote(-1) }) {
                    Icon(Icons.Default.ThumbDown, contentDescription = "Downvote")
                }
                // Note: DistrictPoll doesn't have a direct 'downvotes' field, 
                // using sum of votes or specific logic as per your model
            }
        }
    }
}
