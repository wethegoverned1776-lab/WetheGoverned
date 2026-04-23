package net.wetheGoverned.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import net.wetheGoverned.model.CivicPoll
import net.wetheGoverned.model.PollPost
import net.wetheGoverned.model.ResidentProfile
import net.wetheGoverned.repository.PollRepositoryImpl
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * P2P Mesh Engine: Every app is a relay/server.
 * Syncs Polls, Posts, and Resident Profiles across the network.
 */
@Singleton
class P2PSyncEngine @Inject constructor(
    private val pollRepository: PollRepositoryImpl,
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager,
    private val discoveryManager: MeshDiscoveryManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var server: ApplicationEngine? = null
    private val PORT = 8080
    
    private var syncIntervalMs = 15000L
    private val isLowPowerMode = AtomicBoolean(false)

    fun adjustPerformance(lowPowerMode: Boolean) {
        isLowPowerMode.set(lowPowerMode)
        syncIntervalMs = if (lowPowerMode) 60000L else 15000L
    }

    fun startServer() {
        if (server != null) return
        
        val districtId = sessionManager.currentSession?.districtId ?: "unknown"
        
        server = embeddedServer(Netty, port = PORT, host = "0.0.0.0") {
            install(ServerContentNegotiation) {
                json()
            }
            routing {
                get("/handshake") {
                    call.respond(mapOf("districtId" to districtId, "status" to "active"))
                }
                get("/residents") {
                    // This app-server shares all residents it knows about
                    val residents = residentRepository.observeProfile("").let { 
                        // In a real implementation, we'd have a 'getAllResidents' in DAO
                        emptyList<ResidentProfile>() 
                    }
                    call.respond(residents)
                }
                get("/polls") {
                    val polls = pollRepository.getDistrictPollsSync(districtId)
                    call.respond(polls)
                }
                get("/posts") {
                    val posts = pollRepository.getAllPosts()
                    call.respond(posts)
                }
            }
        }.start(wait = false)
        
        discoveryManager.registerService(PORT, districtId)
        discoveryManager.discoverPeers()
        startSyncLoop()
    }

    private fun startSyncLoop() {
        scope.launch {
            val client = HttpClient {
                install(ClientContentNegotiation) { 
                    json(kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    })
                }
                install(HttpTimeout) { requestTimeoutMillis = 5000 }
            }
            
            while (isActive) {
                delay(syncIntervalMs)
                
                val currentDistrict = sessionManager.currentSession?.districtId ?: continue
                val peers = discoveryManager.discoveredPeers.value

                for (peer in peers) {
                    try {
                        val handshake: Map<String, String> = client.get("http://$peer:$PORT/handshake").body()
                        
                        if (handshake["districtId"] == currentDistrict) {
                            // Sync Residents (Users)
                            val remoteResidents: List<ResidentProfile> = client.get("http://$peer:$PORT/residents").body()
                            remoteResidents.forEach { residentRepository.updateProfile(it.pubKey, it.displayName, it.bio, it.avatarUrl) }

                            // Sync Polls
                            val remotePolls: List<CivicPoll> = client.get("http://$peer:$PORT/polls").body()
                            remotePolls.forEach { pollRepository.syncPoll(it) }
                            
                            Log.d("P2PSyncEngine", "Mesh sync successful with $peer")
                        }
                    } catch (e: Exception) {
                        Log.v("P2PSyncEngine", "Peer $peer sync skipped: ${e.message}")
                    }
                }
            }
        }
    }

    fun stopServer() {
        discoveryManager.stopDiscovery()
        server?.stop(1000, 5000)
        server = null
    }
}
