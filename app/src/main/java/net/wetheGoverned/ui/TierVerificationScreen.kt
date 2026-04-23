package net.wetheGoverned.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.wetheGoverned.ui.verification.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TierVerificationScreen(
    onBack: () -> Unit,
    viewModel: TierVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verified Constituent Onboarding") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Voter Identification",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Verify your status as a registered voter to participate in governance.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            if (uiState.state == VerificationState.SUCCESS) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color.Green, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("VERIFIED CONSTITUENT", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Florida District 06", color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                            Text("Start Voting")
                        }
                    }
                }
            } else {
                item {
                    OutlinedTextField(
                        value = uiState.firstName,
                        onValueChange = viewModel::onFirstNameChange,
                        label = { Text("Legal First Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = uiState.lastName,
                        onValueChange = viewModel::onLastNameChange,
                        label = { Text("Legal Last Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = uiState.streetAddress,
                        onValueChange = viewModel::onStreetAddressChange,
                        label = { Text("Street Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = uiState.city,
                            onValueChange = viewModel::onCityChange,
                            label = { Text("City") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.zipCode,
                            onValueChange = viewModel::onZipCodeChange,
                            label = { Text("Zip") },
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }

                if (uiState.state == VerificationState.DETECTING_DISTRICTS || uiState.state == VerificationState.VERIFYING_ROLLS) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(Modifier.padding(16.dp).fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (uiState.detectedCongressional != null) Icon(Icons.Default.CheckCircle, "", tint = Color.Green)
                                    else CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Detecting Congressional Districts...")
                                }
                                if (uiState.detectedCongressional != null) {
                                    Text("→ ${uiState.detectedCongressional}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 24.dp))
                                }
                                
                                Spacer(Modifier.height(8.dp))
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (uiState.state == VerificationState.VERIFYING_ROLLS) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                                    else if (uiState.state == VerificationState.SUCCESS) Icon(Icons.Default.CheckCircle, "", tint = Color.Green)
                                    else Icon(Icons.Default.Map, "", tint = Color.Gray)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Verifying Voter Registration...")
                                }
                            }
                        }
                    }
                }

                if (uiState.error != null) {
                    item {
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                    }
                }

                item {
                    Button(
                        onClick = { viewModel.startVerificationFlow() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.state == VerificationState.IDLE
                    ) {
                        Text("Verify My Registration")
                    }
                }
                
                item {
                    TextButton(onClick = { /* Link to Vote.org */ }) {
                        Text("Not registered? Register to vote here", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
