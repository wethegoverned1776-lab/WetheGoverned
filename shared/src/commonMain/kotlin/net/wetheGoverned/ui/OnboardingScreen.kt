package net.wetheGoverned.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState.currentStep) {
                OnboardingStep.WELCOME -> {
                    Text("Welcome to WeTheGoverned", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Text("Your gateway to decentralized governance and sovereign identity.", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = { viewModel.refreshStep() }) {
                        Text("Get Started")
                    }
                }
                OnboardingStep.COMPLETED -> {
                    Icon(Icons.Default.CheckCircle, null, tint = Color.Green, modifier = Modifier.size(64.dp))
                    Text("Setup Complete", style = MaterialTheme.typography.headlineSmall)
                    Button(onClick = onOnboardingComplete) {
                        Text("Enter Dashboard")
                    }
                }
                else -> {
                    Text("Setting up your secure node...", style = MaterialTheme.typography.titleMedium)
                    CircularProgressIndicator(Modifier.padding(16.dp))
                    // Auto-advance for demo
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(2000)
                        onOnboardingComplete()
                    }
                }
            }
            
            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
            }
            
            TextButton(onClick = onLogout, modifier = Modifier.padding(top = 32.dp)) {
                Text("Logout")
            }
        }
    }
}
