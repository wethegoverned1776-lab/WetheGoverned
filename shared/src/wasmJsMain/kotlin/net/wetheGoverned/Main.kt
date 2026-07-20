package net.wetheGoverned

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import net.wetheGoverned.App
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.*
import net.wetheGoverned.data.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.getElementById("compose-target")!!) {
        val sessionStorage = remember { WebSessionStorage() }
        val sessionManager = remember { SessionManager(sessionStorage) }
        
        val httpClient = remember {
            HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        }
        val civicApi = remember { WebCivicApi(httpClient) }
        val backendApi = remember { WebWtgBackendApi(httpClient) }
        val locationHelper = remember { LocationHelper() }
        
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

        val pollRepository = remember { WebPollRepository(publisher) }
        val accountRepository = remember { WebAccountRepository() }
        val residentRepository = remember { WebResidentRepository() }
        val manifestoRepository = remember { WebManifestoRepository() }
        val scorecardRepository = remember { WebScorecardRepository() }
        val districtRepository = remember { WebDistrictRepository() }
        val communityRepository = remember { WebCommunityRepository(publisher) }
        val requestRepository = remember { WebVerificationRequestRepository() }
        val voteRepository = remember { WebVoteRepository(publisher) }
        
        val syncEngine = remember {
            P2PSyncEngine(
                pollRepository, residentRepository, voteRepository,
                manifestoRepository, communityRepository, accountRepository, sessionManager,
                relayManager
            )
        }

        LaunchedEffect(Unit) {
            syncEngine.start()
        }

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
