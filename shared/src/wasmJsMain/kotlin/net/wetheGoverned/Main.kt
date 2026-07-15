package net.wetheGoverned

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import net.wetheGoverned.App
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.ui.location.LocationHelper
import net.wetheGoverned.data.NostrRelayManager
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val pollRepository = WebPollRepository()
    val accountRepository = WebAccountRepository()
    val residentRepository = WebResidentRepository()
    val manifestoRepository = WebManifestoRepository()
    val scorecardRepository = WebScorecardRepository()
    val districtRepository = WebDistrictRepository()
    val communityRepository = WebCommunityRepository()
    val requestRepository = WebVerificationRequestRepository()
    val sessionStorage = WebSessionStorage()
    val sessionManager = SessionManager(sessionStorage)
    
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    val civicApi = WebCivicApi(httpClient)
    val backendApi = WebWtgBackendApi(httpClient)
    val locationHelper = LocationHelper()
    
    val relayUrls = listOf("wss://nos.lol", "wss://relay.damus.io")
    val relayManager = NostrRelayManager(relayUrls)

    CanvasBasedWindow("WeTheGoverned") {
        App(
            pollRepository = pollRepository,
            accountRepository = accountRepository,
            residentRepository = residentRepository,
            manifestoRepository = manifestoRepository,
            scorecardRepository = scorecardRepository,
            districtRepository = districtRepository,
            communityRepository = communityRepository,
            requestRepository = requestRepository,
            sessionManager = sessionManager,
            civicApi = civicApi,
            backendApi = backendApi,
            locationHelper = locationHelper,
            relayManager = relayManager
        )
    }
}
