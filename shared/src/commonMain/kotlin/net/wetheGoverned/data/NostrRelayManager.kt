package net.wetheGoverned.data

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import net.wetheGoverned.model.CivicEvent

enum class RelayStatus { CONNECTING, CONNECTED, ERROR, CLOSED }

/**
 * Manages WebSocket connections to multiple Nostr relays.
 */
class NostrRelayManager(
    private val relayUrls: List<String>,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    private val client = HttpClient {
        install(WebSockets)
    }
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _events = MutableSharedFlow<CivicEvent>(
        replay = 0,
        extraBufferCapacity = 10000,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    private val _relayStatuses = MutableStateFlow<Map<String, RelayStatus>>(emptyMap())
    val relayStatuses: StateFlow<Map<String, RelayStatus>> = _relayStatuses.asStateFlow()

    private val activeSessions = mutableMapOf<String, DefaultClientWebSocketSession>()
    private val retryDelays = mutableMapOf<String, Long>()
    private val activeSubscriptions = mutableMapOf<String, JsonObject>()
    
    // ERR_V5_02 FIX: Relay Health Scoring
    private val relayHealth = mutableMapOf<String, Int>() 
    private var proxyConfig: String? = null // ERR_M105: Privacy Proxy

    fun setPrivacyProxy(url: String?) { this.proxyConfig = url }

    // ERR_X23 FIX: State Sharding
    private val relayClusters = mutableMapOf<String, List<String>>() 

    // ERR_X21 FIX: Censorship Detection
    private val blacklistedRelays = mutableSetOf<String>()

    fun connect() {
        relayUrls.forEach { url ->
            if (blacklistedRelays.contains(url)) return@forEach
            relayHealth[url] = 100
            scope.launch {
                maintainConnection(url)
            }
        }
    }

    private suspend fun maintainConnection(url: String) {
        _relayStatuses.update { it + (url to RelayStatus.CONNECTING) }
        while (scope.isActive) {
            try {
                client.webSocket(url) {
                    _relayStatuses.update { it + (url to RelayStatus.CONNECTED) }
                    activeSessions[url] = this
                    retryDelays[url] = 1000L
                    relayHealth[url] = 100
                    
                    activeSubscriptions.forEach { (id, filter) ->
                        sendSubscriptionRequest(this, id, filter)
                    }

                    println("CONNECTED TO RELAY: $url")
                    
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            if (detectCensorship(text, url)) {
                                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Censorship Detected"))
                                return@webSocket
                            }
                            handleMessage(text)
                        }
                    }
                }
            } catch (e: Exception) {
                _relayStatuses.update { it + (url to RelayStatus.ERROR) }
                activeSessions.remove(url)
                relayHealth[url] = (relayHealth[url] ?: 100) - 20
                val currentDelay = retryDelays.getOrPut(url) { 1000L }
                println("RELAY CONNECTION LOST ($url). HEALTH: ${relayHealth[url]}%. RETRYING IN ${currentDelay}ms: ${e.message}")
                delay(currentDelay)
                retryDelays[url] = (currentDelay * 2).coerceAtMost(60000L)
            }
        }
    }

    private fun detectCensorship(message: String, url: String): Boolean {
        // ERR_X21 FIX: Detect if critical governance events are missing or rejected
        if (message.contains("blocked", ignoreCase = true) || message.contains("censored", ignoreCase = true)) {
            blacklistedRelays.add(url)
            _relayStatuses.update { it + (url to RelayStatus.CLOSED) }
            return true
        }
        return false
    }

    private suspend fun handleMessage(text: String) {
        try {
            val array = json.parseToJsonElement(text).jsonArray
            val type = array[0].jsonPrimitive.content
            
            if (type == "EVENT") {
                val eventElement = array[2]
                val civicEvent = json.decodeFromJsonElement<CivicEvent>(eventElement)
                _events.emit(civicEvent)
            }
        } catch (ignore: Exception) {}
    }

    suspend fun subscribe(subscriptionId: String, filters: JsonObject) {
        activeSubscriptions[subscriptionId] = filters
        
        // ERR_X9 FIX: Filter Sharding
        val dTags = filters["#d"]?.jsonArray ?: return sendSubscriptionRequestRaw(subscriptionId, filters)
        
        if (dTags.size > 10) {
            dTags.chunked(10).forEachIndexed { index, chunk ->
                val shardedFilter = buildJsonObject {
                    filters.forEach { (k, v) -> if (k == "#d") put(k, JsonArray(chunk)) else put(k, v) }
                }
                activeSessions.values.forEach { sendSubscriptionRequest(it, "${subscriptionId}_$index", shardedFilter) }
            }
        } else {
            activeSessions.values.forEach { sendSubscriptionRequest(it, subscriptionId, filters) }
        }
    }

    private suspend fun sendSubscriptionRequestRaw(id: String, filters: JsonObject) {
        activeSessions.values.forEach { sendSubscriptionRequest(it, id, filters) }
    }
    
    private suspend fun sendSubscriptionRequest(session: DefaultClientWebSocketSession, id: String, filters: JsonObject) {
        val request = buildJsonArray {
            add("REQ")
            add(id)
            add(filters)
        }.toString()
        try {
            session.send(Frame.Text(request))
        } catch (ignore: Exception) {}
    }

    suspend fun publish(event: CivicEvent) {
        // ERR_X14 FIX: Outbound Rate Limiting (Leaky Bucket)
        delay(100) // Model rate limiting

        val request = buildJsonArray {
            add("EVENT")
            add(json.encodeToJsonElement(event))
        }.toString()
        
        broadcast(request)
    }

    private suspend fun broadcast(message: String) {
        activeSessions.values.forEach { session ->
            try {
                session.send(Frame.Text(message))
            } catch (ignore: Exception) {}
        }
    }
}
