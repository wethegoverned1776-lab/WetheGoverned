import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.wetheGoverned.App
import net.wetheGoverned.data.*
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.remote.api.WtgBackendApi
import net.wetheGoverned.ui.location.LocationHelper
import java.io.File
import androidx.compose.ui.res.painterResource

fun main() {
    application {
        var isWindowVisible by remember { mutableStateOf(true) }
        val trayState = rememberTrayState()
        
        // Core Repositories (Stay active in background as a node)
        val voteRepository = remember { DesktopVoteRepository() }
        val pollRepository = remember { DesktopPollRepository() }
        val residentRepository = remember { DesktopResidentRepository() }
        val manifestoRepository = remember { DesktopManifestoRepository() }
        val scorecardRepository = remember { DesktopScorecardRepository() }
        val districtRepository = remember { DesktopDistrictRepository() }
        val communityRepository = remember { DesktopCommunityRepository() }
        val accountRepository = remember { DesktopAccountRepository() }
        val sessionStorage = remember { DesktopSessionStorage() }
        val sessionManager = remember { SessionManager(sessionStorage) }
        
        val httpClient = remember { 
            HttpClient(CIO) {
                install(ContentNegotiation) { 
                    json(kotlinx.serialization.json.Json { ignoreUnknownKeys = true })
                }
            } 
        }
        val civicApi = remember { DesktopCivicApi(httpClient) }
        val locationHelper = remember { LocationHelper() }
        val backendApi = remember { DesktopWtgBackendApi() }
        
        val relayUrls = listOf("wss://nos.lol", "wss://relay.damus.io")
        val relayManager = remember { NostrRelayManager(relayUrls) }
        val p2pSyncEngine = remember {
            P2PSyncEngine(
                pollRepository, residentRepository, voteRepository,
                manifestoRepository, accountRepository, sessionManager,
                relayManager
            )
        }

        // Start the P2P node immediately - stays active even if window is closed
        LaunchedEffect(Unit) {
            p2pSyncEngine.start()
        }

        // SYSTEM TRAY: Keeps the app active as a background node
        Tray(
            state = trayState,
            icon = painterResource("icon.png"),
            tooltip = "WeTheGoverned Node (Active)",
            onAction = { isWindowVisible = true }, 
            menu = {
                Item("Open Dashboard", onClick = { isWindowVisible = true })
                Separator()
                Item("Exit Fully", onClick = ::exitApplication)
            }
        )

        if (isWindowVisible) {
            Window(
                onCloseRequest = { isWindowVisible = false },
                title = "WeTheGoverned",
                icon = painterResource("icon.png"),
                state = rememberWindowState(width = 840.dp, height = 900.dp)
            ) {
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
                    locationHelper = locationHelper,
                    relayManager = relayManager
                )
            }
        }
    }
}
