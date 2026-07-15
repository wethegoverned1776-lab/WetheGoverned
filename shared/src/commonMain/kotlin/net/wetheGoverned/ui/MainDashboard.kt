package net.wetheGoverned.ui

import androidx.compose.runtime.Composable
import net.wetheGoverned.ui.home.HomeScreen

/**
 * Production alias for the primary app landing screen.
 */
@Composable
fun MainDashboard(
    viewModel: HomeViewModel,
    onNavigateToPoll: (String) -> Unit,
    onNavigateToDistrictSelection: () -> Unit,
    onNavigateToManifestos: () -> Unit,
    onNavigateToMetrics: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToCommunityHub: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onCreatePoll: () -> Unit,
    onLogout: () -> Unit
) {
    HomeScreen(
        viewModel = viewModel,
        onNavigateToPoll = onNavigateToPoll,
        onNavigateToDistrictSelection = onNavigateToDistrictSelection,
        onNavigateToManifestos = onNavigateToManifestos,
        onNavigateToMetrics = onNavigateToMetrics,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToCommunityHub = onNavigateToCommunityHub,
        onNavigateToVerification = onNavigateToVerification,
        onCreatePoll = onCreatePoll,
        onLogout = onLogout
    )
}
