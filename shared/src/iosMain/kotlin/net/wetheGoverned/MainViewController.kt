package net.wetheGoverned

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.remote.api.WtgBackendApi
import net.wetheGoverned.ui.location.LocationHelper

/**
 * iOS Entry point for Compose Multiplatform.
 * This is called from the Swift side (Xcode project).
 */
fun MainViewController(
    pollRepository: PollRepository,
    accountRepository: AccountRepository,
    residentRepository: ResidentRepository,
    manifestoRepository: ManifestoRepository,
    scorecardRepository: ScorecardRepository,
    districtRepository: DistrictRepository,
    communityRepository: CommunityRepository,
    sessionManager: SessionManager,
    civicApi: CivicApi,
    backendApi: WtgBackendApi,
    locationHelper: LocationHelper,
): UIViewController = ComposeUIViewController {
    App(
        pollRepository = pollRepository,
        accountRepository = accountRepository,
        residentRepository = residentRepository,
        manifestoRepository = manifestoRepository,
        scorecardRepository = scorecardRepository,
        districtRepository = districtRepository,
        communityRepository = communityRepository,
        sessionManager = sessionManager,
        civicApi = civicApi,
        backendApi = backendApi,
        locationHelper = locationHelper
    )
}
