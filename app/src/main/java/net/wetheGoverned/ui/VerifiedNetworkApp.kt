package net.wetheGoverned.ui

import androidx.compose.runtime.Composable
import net.wetheGoverned.App
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.remote.api.WtgBackendApi
import net.wetheGoverned.ui.location.LocationHelper
import javax.inject.Inject
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.EntryPointAccessors

@Composable
fun VerifiedNetworkApp() {
    val context = LocalContext.current
    val entryPoint = remember(context) {
        EntryPointAccessors.fromApplication(context, AppEntryPoint::class.java)
    }

    App(
        pollRepository = entryPoint.pollRepository(),
        accountRepository = entryPoint.accountRepository(),
        residentRepository = entryPoint.residentRepository(),
        manifestoRepository = entryPoint.manifestoRepository(),
        scorecardRepository = entryPoint.scorecardRepository(),
        communityRepository = entryPoint.communityRepository(),
        requestRepository = entryPoint.requestRepository(),
        sessionManager = entryPoint.sessionManager(),
        civicApi = entryPoint.civicApi(),
        backendApi = entryPoint.wtgBackendApi(),
        locationHelper = entryPoint.locationHelper(),
        relayManager = entryPoint.relayManager() // Loop 2 Fix
    )
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface AppEntryPoint {
    fun pollRepository(): PollRepository
    fun accountRepository(): AccountRepository
    fun residentRepository(): ResidentRepository
    fun manifestoRepository(): ManifestoRepository
    fun scorecardRepository(): ScorecardRepository
    fun districtRepository(): DistrictRepository
    fun communityRepository(): CommunityRepository
    fun requestRepository(): VerificationRequestRepository
    fun sessionManager(): SessionManager
    fun civicApi(): CivicApi
    fun wtgBackendApi(): WtgBackendApi
    fun locationHelper(): LocationHelper
    fun relayManager(): net.wetheGoverned.data.NostrRelayManager
}
