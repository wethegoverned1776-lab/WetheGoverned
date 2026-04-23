package net.wetheGoverned.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    onNavigateToDistrictSelection: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var mnemonicInput by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthenticated()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = when(authMode) {
                        AuthMode.LOGIN -> "Login"
                        AuthMode.REGISTER -> "Create Account"
                        AuthMode.IMPORT -> "Import Account"
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                if (uiState.generatedMnemonic != null) {
                    MnemonicDisplay(
                        mnemonic = uiState.generatedMnemonic!!,
                        onConfirmed = {
                            viewModel.clearMnemonic()
                            authMode = AuthMode.LOGIN
                        }
                    )
                } else {
                    if (authMode == AuthMode.IMPORT) {
                        OutlinedTextField(
                            value = mnemonicInput,
                            onValueChange = { mnemonicInput = it },
                            label = { Text("12-Word Recovery Phrase") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("word1 word2 ... word12") }
                        )
                    }

                    if (authMode == AuthMode.REGISTER) {
                        Text("Voting district (Optional during reg):", style = MaterialTheme.typography.bodySmall)
                        OutlinedButton(
                            onClick = onNavigateToDistrictSelection,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(uiState.selectedDistrictName)
                        }
                    }

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    )

                    if (authMode == AuthMode.REGISTER) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        )
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    )

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            when(authMode) {
                                AuthMode.LOGIN -> viewModel.login(username, password)
                                AuthMode.REGISTER -> viewModel.register(username, password, displayName)
                                AuthMode.IMPORT -> viewModel.importAccount(mnemonicInput, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (authMode == AuthMode.LOGIN) "Login" else "Continue")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { 
                            authMode = if (authMode == AuthMode.REGISTER) AuthMode.LOGIN else AuthMode.REGISTER 
                        }) {
                            Text(if (authMode == AuthMode.REGISTER) "Back to Login" else "Register")
                        }
                        
                        TextButton(onClick = { 
                            authMode = if (authMode == AuthMode.IMPORT) AuthMode.LOGIN else AuthMode.IMPORT 
                        }) {
                            Text(if (authMode == AuthMode.IMPORT) "Back to Login" else "Import Secret Phrase")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MnemonicDisplay(mnemonic: String, onConfirmed: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "IMPORTANT: Backup your Recovery Phrase",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "If you lose this phrase and delete the app, you will lose access to your verified status forever.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.1f))
                    .padding(12.dp)
            ) {
                Text(
                    text = mnemonic,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(Modifier.height(16.dp))
            Button(onClick = onConfirmed) {
                Text("I have written it down")
            }
        }
    }
}

enum class AuthMode { LOGIN, REGISTER, IMPORT }
