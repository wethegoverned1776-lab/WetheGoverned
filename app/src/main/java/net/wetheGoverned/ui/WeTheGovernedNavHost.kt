package net.wetheGoverned.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.wetheGoverned.ui.home.HomeScreen
import net.wetheGoverned.ui.CreatePollScreen

object CivicRoutes {
    const val AUTH             = "auth"
    const val HOME             = "home"
    const val POLL_DETAIL      = "poll/{pollId}"
    const val DISTRICT_SELECT  = "districts"
    const val AUTH_DISTRICT_SELECT = "auth_districts"
    const val MANIFESTOS       = "manifestos"
    const val MANIFESTO        = "manifesto/{manifestoId}"
    const val METRICS          = "metrics"
    const val PROFILE          = "profile/{pubKey}"
    const val VERIFICATION     = "verification"
    const val CREATE_POLL      = "poll/create"
    const val DISCUSSION       = "poll/{pollId}/discussion/{optionId}"
    const val POST_DETAIL      = "post/{postId}"

    fun pollDetail(pollId: String)     = "poll/$pollId"
    fun manifesto(manifestoId: String) = "manifesto/$manifestoId"
    fun profile(pubKey: String)        = "profile/$pubKey"
    fun discussion(pollId: String, optionId: String) = "poll/$pollId/discussion/$optionId"
    fun postDetail(postId: String)     = "post/$postId"
}

@Composable
fun WeTheGovernedNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = CivicRoutes.AUTH,
    snackbarController: SnackbarController
) {
    // We create the ViewModels here so they are scoped to the lifecycle of the NavHost (the Activity)
    // This ensures state is preserved when navigating between auth screens or home/districts.
    val authViewModel: AuthViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(CivicRoutes.AUTH) {
            AuthScreen(
                onAuthenticated = {
                    Log.i("Navigation", "Auth successful. Navigating to HOME.")
                    // Force refresh HomeViewModel state before navigating
                    homeViewModel.refreshSession()
                    navController.navigate(CivicRoutes.HOME) {
                        popUpTo(CivicRoutes.AUTH) { inclusive = true }
                    }
                },
                onNavigateToDistrictSelection = {
                    navController.navigate(CivicRoutes.AUTH_DISTRICT_SELECT)
                },
                viewModel = authViewModel
            )
        }

        composable(CivicRoutes.AUTH_DISTRICT_SELECT) {
            DistrictSelectionScreen(
                onDistrictSelected = { id, name ->
                    authViewModel.onDistrictSelected(id, name)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(CivicRoutes.HOME) {
            HomeScreen(
                onNavigateToPoll              = { navController.navigate(CivicRoutes.pollDetail(it)) },
                onNavigateToDistrictSelection = { navController.navigate(CivicRoutes.DISTRICT_SELECT) },
                onNavigateToManifestos        = { navController.navigate(CivicRoutes.MANIFESTOS) },
                onNavigateToMetrics           = { navController.navigate(CivicRoutes.METRICS) },
                onNavigateToProfile           = { 
                    Log.i("Navigation", "Navigating to Profile: $it")
                    navController.navigate(CivicRoutes.profile(it)) 
                },
                onCreatePoll                  = { navController.navigate(CivicRoutes.CREATE_POLL) },
                onLogout                      = {
                    navController.navigate(CivicRoutes.AUTH) {
                        popUpTo(CivicRoutes.HOME) { inclusive = true }
                    }
                },
                viewModel = homeViewModel
            )
        }

        composable(CivicRoutes.DISTRICT_SELECT) {
            DistrictSelectionScreen(
                onDistrictSelected = { id, name ->
                    homeViewModel.selectDistrict(id, name)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(CivicRoutes.CREATE_POLL) {
            CreatePollScreen(
                onBack = { navController.popBackStack() },
                onCreate = { pollId ->
                    navController.navigate(CivicRoutes.pollDetail(pollId)) {
                        popUpTo(CivicRoutes.CREATE_POLL) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = CivicRoutes.POLL_DETAIL,
            arguments = listOf(navArgument("pollId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pollId = backStackEntry.arguments?.getString("pollId") ?: return@composable
            PollDetailScreen(
                pollId = pollId,
                onBack = { navController.popBackStack() },
                onNavigateToDiscussion = { pId, oId -> 
                    navController.navigate(CivicRoutes.discussion(pId, oId))
                }
            )
        }

        composable(
            route = CivicRoutes.DISCUSSION,
            arguments = listOf(
                navArgument("pollId") { type = NavType.StringType },
                navArgument("optionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pollId = backStackEntry.arguments?.getString("pollId") ?: return@composable
            val optionId = backStackEntry.arguments?.getString("optionId") ?: return@composable
            PollDiscussionScreen(
                pollId = pollId,
                optionId = optionId,
                onBack = { navController.popBackStack() },
                onNavigateToPost = { postId ->
                    navController.navigate(CivicRoutes.postDetail(postId))
                }
            )
        }

        composable(
            route = CivicRoutes.POST_DETAIL,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
            PollPostDetailScreen(
                postId = postId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(CivicRoutes.MANIFESTOS) {
            ManifestoListScreen(
                onBack = { navController.popBackStack() },
                onManifestoClick = { id -> navController.navigate(CivicRoutes.manifesto(id)) }
            )
        }

        composable(
            route = CivicRoutes.MANIFESTO,
            arguments = listOf(navArgument("manifestoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val manifestoId = backStackEntry.arguments?.getString("manifestoId") ?: return@composable
            ManifestoDetailScreen(
                manifestoId = manifestoId,
                onBack = { navController.popBackStack() }
            )
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
                onUpgradeTier = { navController.navigate(CivicRoutes.VERIFICATION) }
            )
        }

        composable(CivicRoutes.VERIFICATION) {
            TierVerificationScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
