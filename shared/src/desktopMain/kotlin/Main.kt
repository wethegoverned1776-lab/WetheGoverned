package net.wetheGoverned

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
import io.ktor.http.*
import net.wetheGoverned.App
import net.wetheGoverned.data.*
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.remote.api.WtgBackendApi
import java.io.File
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.flow.flowOf

fun main() {
    application {
        var isWindowVisible by remember { mutableStateOf(true) }
        val trayState = rememberTrayState()
        
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
        
        val publisher = remember {
            WsCivicPublisher(
                relayManager, sessionManager, 
                object : net.wetheGoverned.session.PendingEventQueue {
                    override suspend fun enqueue(kind: Int, contentJson: String, sig: String) {}
                    override suspend fun getAllPending(): List<net.wetheGoverned.session.PendingEvent> = emptyList()
                    override suspend fun dequeue(eventId: String) {}
                },
                object : net.wetheGoverned.zk.ZkProver {
                    override suspend fun generateProof(circuitName: String, inputs: Map<String, Any>): net.wetheGoverned.zk.ZkProofResult = 
                        net.wetheGoverned.zk.ZkProofResult(emptyList(), emptyList())
                }
            )
        }

        // Core Repositories
        val voteRepository = remember { DesktopVoteRepository(publisher) }
        val pollRepository = remember { DesktopPollRepository(publisher) }
        val residentRepository = remember { DesktopResidentRepository() }
        val manifestoRepository = remember { DesktopManifestoRepository() }
        val scorecardRepository = remember { DesktopScorecardRepository() }
        val communityRepository = remember { DesktopCommunityRepository(publisher) }
        val districtRepository = remember { DesktopDistrictRepository() }
        val accountRepository = remember { DesktopAccountRepository() }
        val requestRepository = remember { DesktopVerificationRequestRepository() }
        
        val p2pSyncEngine = remember {
            P2PSyncEngine(
                pollRepository, residentRepository, voteRepository,
                manifestoRepository, communityRepository, accountRepository, sessionManager,
                relayManager
            )
        }

        LaunchedEffect(Unit) {
            p2pSyncEngine.start()
        }

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
                state = rememberWindowState(placement = WindowPlacement.Maximized)
            ) {
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
    }
}
