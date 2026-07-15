package net.wetheGoverned.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    AuthContent(
        uiState = uiState,
        onLogin = viewModel::login,
        onChangePassword = viewModel::changePassword,
        onLoginAsGuest = { viewModel.loginAsGuest() },
        onNavigateToChangePassword = onNavigateToChangePassword,
        onAuthenticated = onAuthenticated,
        onBack = onBack
    )
}

@Composable
fun AuthContent(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onChangePassword: (String, String) -> Unit,
    onLoginAsGuest: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onAuthenticated: () -> Unit,
    onBack: () -> Unit
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // UX_L02 FIX

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isAuthenticated, uiState.requiresPasswordChange) {
        if (uiState.isAuthenticated) {
            if (uiState.requiresPasswordChange) {
                onNavigateToChangePassword()
            } else {
                onAuthenticated()
            }
        }
    }

    LaunchedEffect(uiState.isPasswordChanged) {
        if (uiState.isPasswordChanged) {
            snackbarHostState.showSnackbar("Password updated successfully. Please login.")
            authMode = AuthMode.LOGIN
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = when(authMode) {
                        AuthMode.LOGIN -> "Login"
                        AuthMode.CHANGE_PASSWORD -> "Reset Password"
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (authMode == AuthMode.LOGIN) "Password" else "Current Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Toggle Visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                if (authMode == AuthMode.CHANGE_PASSWORD) {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    )
                }

                if (uiState.error != null) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            when(authMode) {
                                AuthMode.LOGIN -> onLogin(username, password)
                                AuthMode.CHANGE_PASSWORD -> onChangePassword(username, newPassword)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (authMode == AuthMode.LOGIN) "Login" else "Update")
                        }
                    }

                    if (authMode == AuthMode.LOGIN) {
                        OutlinedButton(
                            onClick = onLoginAsGuest,
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isLoading
                        ) {
                            Text("Observer")
                        }
                    }
                }

                TextButton(
                    onClick = onNavigateToChangePassword,
                ) {
                    Text("Change Password")
                }
                
                Text(
                    "Registration is available via invitation from verified users.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )

                if (password.startsWith("temp_") && authMode == AuthMode.LOGIN) {
                    Text(
                        "Note: It looks like you're using a temporary password. Please switch to 'Change Password' to update it.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

enum class AuthMode { LOGIN, CHANGE_PASSWORD }
