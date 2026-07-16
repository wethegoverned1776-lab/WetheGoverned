package net.wetheGoverned

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.withContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.ui.*
import net.wetheGoverned.ui.home.HomeScreen
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.remote.api.WtgBackendApi
import net.wetheGoverned.ui.components.USFlagBackground
import net.wetheGoverned.ui.WelcomeScreen
import androidx.compose.foundation.layout.Box
import net.wetheGoverned.ui.community.CommunityBoardScreen

object SharedRoutes {
    const val WELCOME = "welcome"
    const val AUTH = "auth"
    const val CHANGE_PASSWORD = "change_password"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val POLL_DETAIL = "poll/{pollId}"
    const val PROFILE = "profile/{pubKey}"
    const val JURISDICTION_SELECT = "jurisdictions"
    const val METRICS = "metrics"
    const val MANIFESTOS = "manifestos"
    const val MANIFESTO_DETAIL = "manifesto/{manifestoId}"
    const val CREATE_POLL = "create_poll"
    const val SCORECARD = "scorecard"
    const val DISCUSSION = "poll/{pollId}/discussion/{optionId}"
    const val POST_DETAIL = "post/{postId}"
    const val VERIFICATION = "verification"
    const val GOVERNANCE = "governance"
    const val COMMUNITY_HUB = "community_hub"
    const val NETWORK_REGISTRATION = "network_registration"
    
    fun pollDetail(id: String) = "poll/$id"
    fun profile(pubKey: String) = "profile/$pubKey"
    fun manifestoDetail(id: String) = "manifesto/$id"
    fun discussion(pollId: String, optionId: String) = "poll/$pollId/discussion/$optionId"
    fun postDetail(postId: String) = "post/$postId"
}

@Composable
fun App(
    pollRepository: PollRepository,
    accountRepository: AccountRepository,
    residentRepository: ResidentRepository,
    manifestoRepository: ManifestoRepository,
    scorecardRepository: ScorecardRepository,
    districtRepository: DistrictRepository, // Added
    communityRepository: CommunityRepository,
    requestRepository: VerificationRequestRepository,
    sessionManager: SessionManager,
    civicApi: CivicApi,
    backendApi: WtgBackendApi,
    locationHelper: LocationHelper,
    relayManager: net.wetheGoverned.data.NostrRelayManager,
) {
    val navController = rememberNavController()

    val authViewModel = remember { AuthViewModel(accountRepository, sessionManager, residentRepository) }
    val onboardingViewModel = remember { OnboardingViewModel(civicApi, backendApi, sessionManager, locationHelper) }
    val homeViewModel = remember { HomeViewModel(pollRepository, residentRepository, sessionManager, relayManager) }
    val pollDetailViewModel = remember { PollDetailViewModel(pollRepository, residentRepository, sessionManager) }
    val manifestoViewModel = remember { ManifestoViewModel(manifestoRepository, pollRepository, sessionManager) }
    val profileViewModel = remember { ResidentProfileViewModel(residentRepository, accountRepository, sessionManager, requestRepository) }
    val pollViewModel = remember { PollViewModel(pollRepository, sessionManager) }
    val scorecardViewModel = remember { ScorecardViewModel(scorecardRepository, sessionManager) }
    val discussionViewModel = remember { PollDiscussionViewModel(pollRepository, sessionManager) }
    val postDetailViewModel = remember { PollPostDetailViewModel(pollRepository, sessionManager) }
    val tierVerificationViewModel = remember {
        TierVerificationViewModel(residentRepository, sessionManager, accountRepository, civicApi)
    }
    val communityBoardViewModel = remember {
        CommunityBoardViewModel(communityRepository, sessionManager)
    }
    val networkRegViewModel = remember {
        NetworkRegistrationViewModel(accountRepository, residentRepository, sessionManager)
    }

    // Auto-navigate and refresh data if session exists
    LaunchedEffect(Unit) {
        // ERR_010 FIX: Move to background dispatcher
        withContext(kotlinx.coroutines.Dispatchers.Default) {
            civicApi.refreshDistrictRegistry()
        }

        val session = sessionManager.currentSession
        if (session != null) {
            onboardingViewModel.refreshStep() // Synchronize onboarding state
            if (session.districtId == null) {
                navController.navigate(SharedRoutes.ONBOARDING) {
                    popUpTo(SharedRoutes.WELCOME) { inclusive = true }
                }
            } else {
                navController.navigate(SharedRoutes.HOME) {
                    popUpTo(SharedRoutes.WELCOME) { inclusive = true }
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black,
            outline = Color(0xFFCFD9DE), // Dark grey borders (X style)
            surfaceVariant = Color.White,
            onSurfaceVariant = Color(0xFF536471), // Dimmer grey for secondary text
            secondary = Color(0xFF536471),
            tertiary = Color(0xFF1D9BF0), // Classic X Blue for links/actions
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // USFlagBackground(alpha = 0.15f) // Disabled for clean X-style look

                NavHost(
                    navController = navController,
                    startDestination = SharedRoutes.WELCOME,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(SharedRoutes.WELCOME) {
                        WelcomeScreen(
                            onGetStarted = {
                                navController.navigate(SharedRoutes.AUTH)
                            }
                        )
                    }

                    composable(SharedRoutes.AUTH) {
                        AuthScreen(
                            viewModel = authViewModel,
                            onAuthenticated = {
                                homeViewModel.refreshSession()
                                onboardingViewModel.refreshStep()
                                val session = sessionManager.currentSession
                                val isGuest = session?.pubKey == "guest_observer_hex"
                                
                                if (session?.districtId == null && !isGuest) {
                                    navController.navigate(SharedRoutes.ONBOARDING) {
                                        popUpTo(SharedRoutes.AUTH) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(SharedRoutes.HOME) {
                                        popUpTo(SharedRoutes.AUTH) { inclusive = true }
                                    }
                                }
                            },
                            onNavigateToChangePassword = {
                                navController.navigate(SharedRoutes.CHANGE_PASSWORD)
                            },
                            onBack = {
                                authViewModel.reset()
                                navController.navigate(SharedRoutes.WELCOME) {
                                    popUpTo(SharedRoutes.WELCOME) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(SharedRoutes.CHANGE_PASSWORD) {
                        ChangePasswordScreen(
                            viewModel = authViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(SharedRoutes.ONBOARDING) {
                        OnboardingScreen(
                            viewModel = onboardingViewModel,
                            onOnboardingComplete = {
                                homeViewModel.refreshSession()
                                navController.navigate(SharedRoutes.HOME) {
                                    popUpTo(SharedRoutes.ONBOARDING) { inclusive = true }
                                }
                            },
                            onLogout = {
                                authViewModel.reset()
                                onboardingViewModel.reset()
                                navController.navigate(SharedRoutes.WELCOME) {
                                    popUpTo(SharedRoutes.WELCOME) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(SharedRoutes.HOME) {
                        MainDashboard(
                            viewModel = homeViewModel,
                            onNavigateToPoll = { id -> navController.navigate(SharedRoutes.pollDetail(id)) },
                            onNavigateToDistrictSelection = { navController.navigate(SharedRoutes.JURISDICTION_SELECT) },
                            onNavigateToManifestos = { navController.navigate(SharedRoutes.MANIFESTOS) },
                            onNavigateToMetrics = { navController.navigate(SharedRoutes.METRICS) },
                            onNavigateToProfile = { pubKey -> navController.navigate(SharedRoutes.profile(pubKey)) },
                            onNavigateToCommunityHub = { navController.navigate(SharedRoutes.COMMUNITY_HUB) },
                            onNavigateToVerification = { navController.navigate(SharedRoutes.VERIFICATION) },
                            onCreatePoll = { navController.navigate(SharedRoutes.CREATE_POLL) },
                            onLogout = {
                                authViewModel.reset()
                                onboardingViewModel.reset()
                                navController.navigate(SharedRoutes.WELCOME) {
                                    popUpTo(SharedRoutes.HOME) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(SharedRoutes.JURISDICTION_SELECT) {
                        DistrictSelectionScreen(
                            onDistrictSelected = { id, name ->
                                authViewModel.onDistrictSelected(id, name)
                                homeViewModel.selectDistrict(id, name)
                                profileViewModel.onUpdateDistrict(id)
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = SharedRoutes.POLL_DETAIL,
                        arguments = listOf(navArgument("pollId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val pollId = backStackEntry.arguments?.getString("pollId") ?: return@composable
                        PollDetailScreen(
                            pollId = pollId,
                            viewModel = pollDetailViewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToDiscussion = { pId, oId -> 
                                navController.navigate(SharedRoutes.discussion(pId, oId))
                            }
                        )
                    }

                    composable(
                        route = SharedRoutes.DISCUSSION,
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
                            viewModel = discussionViewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToPost = { postId ->
                                navController.navigate(SharedRoutes.postDetail(postId))
                            }
                        )
                    }

                    composable(
                        route = SharedRoutes.POST_DETAIL,
                        arguments = listOf(navArgument("postId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                        PollPostDetailScreen(
                            postId = postId,
                            viewModel = postDetailViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(SharedRoutes.METRICS) {
                        ScorecardScreen(
                            viewModel = scorecardViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(SharedRoutes.SCORECARD) {
                        ScorecardScreen(
                            viewModel = scorecardViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(SharedRoutes.MANIFESTOS) {
                        ManifestoListScreen(
                            viewModel = manifestoViewModel,
                            onBack = { navController.popBackStack() },
                            onManifestoClick = { id -> navController.navigate(SharedRoutes.manifestoDetail(id)) }
                        )
                    }

                    composable(
                        route = SharedRoutes.MANIFESTO_DETAIL,
                        arguments = listOf(navArgument("manifestoId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val manifestoId = backStackEntry.arguments?.getString("manifestoId") ?: return@composable
                        ManifestoDetailScreen(
                            manifestoId = manifestoId,
                            viewModel = manifestoViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(SharedRoutes.CREATE_POLL) {
                        CreatePollScreen(
                            viewModel = pollViewModel,
                            onBack = { navController.popBackStack() },
                            onCreate = { pollId ->
                                navController.navigate(SharedRoutes.pollDetail(pollId)) {
                                    popUpTo(SharedRoutes.CREATE_POLL) { inclusive = true }
                                }
                            }
                        )
                    }
                    
                    composable(
                        route = SharedRoutes.PROFILE,
                        arguments = listOf(navArgument("pubKey") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val pubKey = backStackEntry.arguments?.getString("pubKey") ?: return@composable
                        ResidentProfileScreen(
                            pubKey = pubKey,
                            viewModel = profileViewModel,
                            onBack = { navController.popBackStack() },
                            onUpgradeTier = { navController.navigate(SharedRoutes.VERIFICATION) },
                            onNavigateToDistrictSelection = { navController.navigate(SharedRoutes.JURISDICTION_SELECT) },
                            onNavigateToRegistration = { navController.navigate(SharedRoutes.NETWORK_REGISTRATION) },
                            onLogout = {
                                authViewModel.reset()
                                onboardingViewModel.reset()
                                navController.navigate(SharedRoutes.WELCOME) {
                                    popUpTo(SharedRoutes.WELCOME) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(SharedRoutes.NETWORK_REGISTRATION) {
                        NetworkRegistrationScreen(
                            viewModel = networkRegViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(SharedRoutes.VERIFICATION) {
                        TierVerificationScreen(
                            viewModel = tierVerificationViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(SharedRoutes.GOVERNANCE) {
                        GovernanceDashboardScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(SharedRoutes.COMMUNITY_HUB) {
                        CommunityBoardScreen(
                            viewModel = communityBoardViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
