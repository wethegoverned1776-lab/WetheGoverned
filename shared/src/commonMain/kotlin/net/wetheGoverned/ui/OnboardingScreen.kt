package net.wetheGoverned.ui

import androidx.compose.runtime.Composable

@Composable
expect fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit,
    onLogout: () -> Unit = {}
)
