package net.wetheGoverned.ui.onboarding

import android.Manifest
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.*

// ─────────────────────────────────────────────────────────────────────────────
// OnboardingScreen  — 5-step animated flow wired to OnboardingViewModel (final)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.step) {
        if (state.step == OnboardingStep.COMPLETE) {
            viewModel.completeOnboarding()
            onOnboardingComplete()
        }
    }

    AnimatedContent(
        targetState = state.step,
        transitionSpec = {
            slideInHorizontally { it } + fadeIn() togetherWith
            slideOutHorizontally { -it } + fadeOut()
        },
        label = "onboarding_step",
    ) { step ->
        when (step) {
            OnboardingStep.WELCOME         -> WelcomeStep(onContinue = viewModel::onWelcomeContinue)
            OnboardingStep.KEY_SETUP       -> KeySetupStep(state, viewModel)
            OnboardingStep.DISTRICT_DETECT -> DistrictStep(state, viewModel)
            OnboardingStep.EMAIL_VERIFY    -> EmailStep(state, viewModel)
            OnboardingStep.COMPLETE        -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

// ── Step 1 : Welcome ──────────────────────────────────────────────────────────

@Composable
private fun WelcomeStep(onContinue: () -> Unit) {
    OnboardingScaffold {
        Spacer(Modifier.weight(1f))
        Surface(
            shape    = MaterialTheme.shapes.extraLarge,
            color    = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(96.dp).align(Alignment.CenterHorizontally),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🏛", style = MaterialTheme.typography.displayMedium)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "WeTheGoverned",
            style    = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "A decentralized civic platform. Participate in district polls, " +
            "track your representative's performance, and make your voice heard " +
            "— secured by Nostr.",
            style     = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.weight(1f))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text("Get Started")
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Open source · Decentralized · Yours",
            style    = MaterialTheme.typography.labelSmall,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

// ── Step 2 : Key Setup ────────────────────────────────────────────────────────

@Composable
private fun KeySetupStep(state: OnboardingUiState, vm: OnboardingViewModel) {
    var showImport  by remember { mutableStateOf(false) }
    var importInput by remember { mutableStateOf("") }
    var showKey     by remember { mutableStateOf(false) }

    OnboardingScaffold {
        StepHeader(1, 4, "Your Identity",
            "WeTheGoverned uses a Nostr keypair as your identity. " +
            "Your private key stays encrypted on this device.")

        Spacer(Modifier.height(24.dp))

        if (state.generatedPubKeyHex != null) {
            KeyReadyCard(
                pubKeyHex   = state.generatedPubKeyHex,
                nsecDisplay = state.nsecDisplay,
                showKey     = showKey,
                onToggleKey = { showKey = !showKey },
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = vm::onKeySetupContinue, modifier = Modifier.fillMaxWidth()) {
                Text("Continue →")
            }
        } else {
            Button(
                onClick  = vm::generateNewKey,
                enabled  = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                }
                Text("Generate New Key")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick  = { showImport = !showImport },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (showImport) "Cancel" else "Import Existing Key") }

            AnimatedVisibility(showImport) {
                Column(Modifier.padding(top = 8.dp)) {
                    var visible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value         = importInput,
                        onValueChange = { importInput = it },
                        label         = { Text("nsec1… or 64-char hex") },
                        modifier      = Modifier.fillMaxWidth(),
                        visualTransformation = if (visible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon  = {
                            IconButton(onClick = { visible = !visible }) {
                                Icon(
                                    if (visible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility",
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine      = true,
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick  = { vm.importExistingKey(importInput) },
                        enabled  = importInput.length >= 32 && !state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Import Key") }
                }
            }
        }

        state.error?.let { ErrorBanner(it, vm::dismissError) }
    }
}

@Composable
private fun KeyReadyCard(
    pubKeyHex: String,
    nsecDisplay: String?,
    showKey: Boolean,
    onToggleKey: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("✓ Key ready", style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Public key:", style = MaterialTheme.typography.labelSmall)
                IconButton(onClick = onToggleKey, modifier = Modifier.size(24.dp)) {
                    Icon(
                        if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle key",
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            if (showKey) {
                SelectionContainer {
                    Text(pubKeyHex,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace))
                }
            } else {
                Text(pubKeyHex.take(12) + "…" + pubKeyHex.takeLast(8),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace))
            }
            if (nsecDisplay != null) {
                HorizontalDivider()
                Text("Back up your private key (nsec) now:",
                    style = MaterialTheme.typography.labelSmall)
                SelectionContainer {
                    Text(nsecDisplay,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// ── Step 3 : District ─────────────────────────────────────────────────────────

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun DistrictStep(state: OnboardingUiState, vm: OnboardingViewModel) {
    val locationPerm = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION,
               Manifest.permission.ACCESS_FINE_LOCATION)
    ) { results ->
        if (results.values.any { it }) {
            vm.detectDistrictFromGps()
        } else {
            vm.onLocationPermissionDenied()
        }
    }
    var manualInput by remember { mutableStateOf(state.manualDistrictId) }

    OnboardingScaffold {
        StepHeader(2, 4, "Your District",
            "We'll auto-detect your congressional district from your location, " +
            "or you can enter it manually (e.g. us-wa-07).")
        Spacer(Modifier.height(24.dp))

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Detecting your district…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            state.detectedDistrict != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("✓ District detected",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary)
                        Text(state.detectedDistrict.displayName,
                            style = MaterialTheme.typography.headlineSmall)
                        Text(state.detectedDistrict.id.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = vm::confirmDistrict, modifier = Modifier.fillMaxWidth()) {
                    Text("This is my district →")
                }
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick  = { vm.onManualDistrictChange("") },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Enter a different district") }
            }

            else -> {
                OutlinedButton(
                    onClick = {
                        if (locationPerm.allPermissionsGranted) {
                            vm.detectDistrictFromGps()
                        } else {
                            locationPerm.launchMultiplePermissionRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled  = !state.locationPermissionDenied,
                ) {
                    Icon(Icons.Default.MyLocation, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (state.locationPermissionDenied)
                            "Location access denied — enter manually"
                        else "Detect from GPS"
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text("— or enter manually —",
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value          = manualInput,
                    onValueChange  = { manualInput = it; vm.onManualDistrictChange(it) },
                    label          = { Text("District ID") },
                    placeholder    = { Text("us-wa-07") },
                    supportingText = { Text("Format: us-[state]-[2-digit number]") },
                    modifier       = Modifier.fillMaxWidth(),
                    singleLine     = true,
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick  = vm::confirmDistrict,
                    enabled  = manualInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Continue →") }
            }
        }

        state.error?.let { ErrorBanner(it, vm::dismissError) }
    }
}

// ── Step 4 : Email ────────────────────────────────────────────────────────────

@Composable
private fun EmailStep(state: OnboardingUiState, vm: OnboardingViewModel) {
    OnboardingScaffold {
        StepHeader(3, 4, "Verify Your Email",
            "Get Tier 1 access. We'll send a magic link — no password needed.")
        Spacer(Modifier.height(24.dp))

        if (state.emailSent) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            ) {
                Column(Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("📧 Check your inbox!", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "A verification link was sent to ${state.email}. " +
                        "Tap the link in that email, or continue now without email verification.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = vm::skipEmailVerification, modifier = Modifier.fillMaxWidth()) {
                Text("Continue to App →")
            }
        } else {
            OutlinedTextField(
                value           = state.email,
                onValueChange   = vm::onEmailChange,
                label           = { Text("Email address") },
                placeholder     = { Text("you@example.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true,
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick  = vm::sendVerificationEmail,
                enabled  = state.email.contains('@') && state.email.contains('.') && !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (state.isLoading) "Sending…" else "Send Verification Link")
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = vm::skipEmailVerification, modifier = Modifier.fillMaxWidth()) {
                Text("Skip for now")
            }
        }

        state.error?.let { ErrorBanner(it, vm::dismissError) }
    }
}

// ── Shared helpers ────────────────────────────────────────────────────────────

@Composable
private fun OnboardingScaffold(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 48.dp),
        content = content,
    )
}

@Composable
private fun StepHeader(step: Int, total: Int, title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LinearProgressIndicator(
            progress = { step.toFloat() / total },
            modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small),
        )
        Text("Step $step of $total",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Spacer(Modifier.height(8.dp))
    Card(
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(message,
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f))
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, "Dismiss",
                    modifier = Modifier.size(16.dp))
            }
        }
    }
}
