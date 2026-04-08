package net.wetheGoverned.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object CivicRoutes {
    const val HOME         = "home"
    const val POLL_DETAIL  = "poll/{pollId}"
    const val SCORECARD    = "scorecard"
    const val MANIFESTOS   = "manifestos"
    const val MANIFESTO    = "manifesto/{manifestoId}"
    const val METRICS      = "metrics"
    const val PROFILE      = "profile/{pubKey}"

    fun pollDetail(pollId: String)     = "poll/$pollId"
    fun manifesto(manifestoId: String) = "manifesto/$manifestoId"
    fun profile(pubKey: String)        = "profile/$pubKey"
}

@Composable
fun WeTheGovernedNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = CivicRoutes.HOME,
    snackbarController: SnackbarController
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(CivicRoutes.HOME) {
            HomeScreen(
                onNavigateToPoll       = { navController.navigate(CivicRoutes.pollDetail(it)) },
                onNavigateToScorecard  = { navController.navigate(CivicRoutes.SCORECARD) },
                onNavigateToManifestos = { navController.navigate(CivicRoutes.MANIFESTOS) },
                onNavigateToMetrics    = { navController.navigate(CivicRoutes.METRICS) },
            )
        }

        composable(
            route = CivicRoutes.POLL_DETAIL,
            arguments = listOf(navArgument("pollId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pollId = backStackEntry.arguments?.getString("pollId") ?: return@composable
            PollDetailScreen(
                pollId = pollId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(CivicRoutes.SCORECARD) {
            ScorecardScreen(
                onBack = { navController.popBackStack() },
                snackbar = snackbarController
            )
        }

        // Manifestos List - using the exact function name and parameters you showed
        composable(CivicRoutes.MANIFESTOS) {
            ManifestoListScreen(
                onBack = { navController.popBackStack() },
                onManifestoClick = { id -> navController.navigate(CivicRoutes.manifesto(id)) }
                // viewModel will be provided by hiltViewModel() inside the screen
            )
        }

        composable(
            route = CivicRoutes.MANIFESTO,
            arguments = listOf(navArgument("manifestoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val manifestoId = backStackEntry.arguments?.getString("manifestoId") ?: return@composable
            // TODO: Call your Manifesto detail screen here when ready
        }

        composable(CivicRoutes.METRICS) {
            MetricsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = CivicRoutes.PROFILE,
            arguments = listOf(navArgument("pubKey") { type = NavType.StringType })
        ) { backStackEntry ->
            val pubKey = backStackEntry.arguments?.getString("pubKey") ?: return@composable
            ResidentProfileScreen(
                pubKey = pubKey,
                onBack = { navController.popBackStack() },
                onUpgradeTier = { /* TODO: navigate to tier verification */ }
            )
        }
    }
}
