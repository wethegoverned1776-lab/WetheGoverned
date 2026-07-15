package net.wetheGoverned.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Governance Dashboard for We The People.
 * Surfaces on-chain voting results, active referendums, and district instructions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GovernanceDashboardScreen(
    districtId: String = "FL-06",
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Governance Hub", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                DistrictHeader(districtId)
            }

            item {
                DashboardCard(
                    title = "Active On-Chain Polls",
                    subtitle = "Immutable voting results from the blockchain",
                    icon = Icons.Default.Gavel,
                    accentColor = Color(0xFF38BDF8)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OnChainPollItem("HR-123 Infrastructure Support", "84% In Favor")
                        OnChainPollItem("District Funding Allocation", "62% For Option B")
                    }
                }
            }

            item {
                DashboardCard(
                    title = "Verified Instructions",
                    subtitle = "Signed directives sent to your representative",
                    icon = Icons.Default.Poll,
                    accentColor = Color(0xFFFBBF24)
                ) {
                    Text(
                        "12 Active instructions pending review by Representative Waltz.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                DashboardCard(
                    title = "Representative Compliance",
                    subtitle = "How well votes align with district polls",
                    icon = Icons.Default.AccountBalance,
                    accentColor = Color(0xFF34D399)
                ) {
                    ComplianceStat("Alignment Score", "92%")
                }
            }
        }
    }
}

@Composable
fun DistrictHeader(districtId: String) {
    Column {
        Text(
            text = "Florida District 06",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Governed by $districtId Residents",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF38BDF8)
        )
    }
}

@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun OnChainPollItem(title: String, result: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Text(result, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
    }
}

@Composable
fun ComplianceStat(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color(0xFF34D399))
    }
}
