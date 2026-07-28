package net.wetheGoverned.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.*
import net.wetheGoverned.model.CivicEvent
import net.wetheGoverned.model.RelayInfo
import net.wetheGoverned.model.RelayMetric

enum class RelayStatus { CONNECTING, CONNECTED, ERROR, CLOSED }

/**
 * Advanced Nostr Relay Manager with NIP-65/66 Discovery and Quality Scoring.
 */
class NostrRelayManager(
    private val initialRelayUrls: List<String>,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    private val client = HttpClient {
        install(WebSockets)
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 5000
        }
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

    // Dynamic Pools
    private val activeSessions = mutableMapOf<String, DefaultClientWebSocketSession>()
    private val relayMetrics = MutableStateFlow<Map<String, RelayMetric>>(emptyMap())
    
    // NIP-65/66 Cache
    private val knownRelays = mutableSetOf<String>().apply { addAll(initialRelayUrls) }
    private val broadcastPool = mutableSetOf<String>()
    private val userRelayLists = mutableMapOf<String, List<String>>() // pubkey -> preferred write relays
    
    private val retryDelays = mutableMapOf<String, Long>()
    private val activeSubscriptions = mutableMapOf<String, List<JsonObject>>()
    
    private var proxyConfig: String? = null

    fun setPrivacyProxy(url: String?) { this.proxyConfig = url }

    private val blacklistedRelays = mutableSetOf<String>()

    init {
        // Background Discovery Loop (Every 4 hours)
        scope.launch {
            while (isActive) {
                performDiscovery()
                delay(4 * 3600 * 1000L) 
            }
        }
    }

    fun connect() {
        initialRelayUrls.forEach { url ->
            if (!blacklistedRelays.contains(url)) {
                scope.launch { maintainConnection(url) }
            }
        }
    }

    private suspend fun performDiscovery() {
        println("🌐 Starting Relay Discovery (NIP-65/66)...")
        // 1. Fetch Discovery Events from current connected relays
        val discoveryFilter = buildJsonObject {
            put("kinds", buildJsonArray { 
                add(JsonPrimitive(10002)) // NIP-65
                add(JsonPrimitive(30066)) // NIP-66
            })
            put("limit", JsonPrimitive(50))
        }
        
        subscribe("discovery_${Clock.System.now().toEpochMilliseconds()}", discoveryFilter)
        
        // Give some time to collect discovery events
        delay(10000)
        
        // 2. Process metrics for known relays
        knownRelays.toList().forEach { url ->
            scope.launch { updateRelayMetric(url) }
        }
        
        // 3. Update Pools based on scores
        delay(5000)
        refreshPools()
    }

    private suspend fun updateRelayMetric(url: String) {
        try {
            val startTime = Clock.System.now().toEpochMilliseconds()
            val infoUrl = url.replace("wss://", "https://").replace("ws://", "http://")
            
            val info: RelayInfo? = try {
                client.get(infoUrl) {
                    header("Accept", "application/nostr+json")
                }.body()
            } catch (e: Exception) { null }

            val endTime = Clock.System.now().toEpochMilliseconds()
            val rtt = endTime - startTime
            
            val metric = RelayMetric(
                url = url,
                rtt = rtt,
                lastSeen = Clock.System.now().toEpochMilliseconds(),
                isOnline = true,
                isPaid = info?.limitation?.payment_required ?: false,
                info = info,
                score = calculateScore(rtt, info)
            )
            
            relayMetrics.update { it + (url to metric) }
        } catch (e: Exception) {
            relayMetrics.update { it + (url to (it[url]?.copy(isOnline = false) ?: RelayMetric(url, isOnline = false))) }
        }
    }

    private fun calculateScore(rtt: Long, info: RelayInfo?): Int {
        var score = 100
        if (rtt > 1000) score -= 20
        if (rtt > 2000) score -= 40
        if (info == null) score -= 30
        if (info?.limitation?.payment_required == true) score -= 50
        return score.coerceAtLeast(0)
    }

    private fun refreshPools() {
        val highQuality = relayMetrics.value.values
            .filter { it.isOnline && !it.isPaid && it.score > 50 }
            .sortedByDescending { it.score }

        // Top 5 for active subscriptions
        val topActive = highQuality.take(5).map { it.url }
        
        // Next 15-50 for broad broadcast
        broadcastPool.clear()
        broadcastPool.addAll(highQuality.take(50).map { it.url })

        // Connect to new high quality relays if not already
        topActive.forEach { url ->
            if (!activeSessions.containsKey(url)) {
                scope.launch { maintainConnection(url) }
            }
        }
    }

    private suspend fun maintainConnection(url: String) {
        if (_relayStatuses.value[url] == RelayStatus.CONNECTED) return
        
        _relayStatuses.update { it + (url to RelayStatus.CONNECTING) }
        while (scope.isActive) {
            try {
                client.webSocket(url) {
                    _relayStatuses.update { it + (url to RelayStatus.CONNECTED) }
                    activeSessions[url] = this
                    retryDelays[url] = 1000L
                    
                    activeSubscriptions.forEach { (id, filters) ->
                        sendSubscriptionRequest(this, id, filters)
                    }

                    println("✅ CONNECTED TO RELAY: $url")
                    
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            handleMessage(text, url)
                        }
                    }
                }
            } catch (e: Exception) {
                _relayStatuses.update { it + (url to RelayStatus.ERROR) }
                activeSessions.remove(url)
                val currentDelay = retryDelays.getOrPut(url) { 1000L }
                delay(currentDelay)
                retryDelays[url] = (currentDelay * 2).coerceAtMost(60000L)
                
                // If it's a broadcast-only relay and it's failing, don't retry forever
                if (!initialRelayUrls.contains(url) && !broadcastPool.contains(url)) return
            }
        }
    }

    private suspend fun handleMessage(text: String, originUrl: String) {
        try {
            val array = json.parseToJsonElement(text).jsonArray
            val type = array[0].jsonPrimitive.content
            
            when (type) {
                "EVENT" -> {
                    val eventElement = array[2]
                    val civicEvent = json.decodeFromJsonElement<CivicEvent>(eventElement)
                    
                    // NIP-65/66 aggregation
                    if (civicEvent.kind == 10002 || civicEvent.kind == 30066) {
                        extractRelaysFromEvent(civicEvent)
                    }
                    
                    _events.emit(civicEvent)
                }
                "OK" -> {
                    // event published successfully
                }
                "NOTICE" -> {
                    println("🔔 NOTICE from $originUrl: ${array[1].jsonPrimitive.content}")
                }
            }
        } catch (ignore: Exception) {}
    }

    private fun extractRelaysFromEvent(event: CivicEvent) {
        if (event.kind == 10002) {
            val preferred = event.tags.filter { it.size >= 2 && it[0] == "r" }
                .filter { it.size == 2 || it[2] == "write" }
                .map { it[1] }
            userRelayLists[event.pubKey] = preferred
        }

        event.tags.forEach { tag ->
            if (tag.size >= 2 && tag[0] == "r") {
                val url = tag[1]
                if (url.startsWith("ws") && !blacklistedRelays.contains(url)) {
                    knownRelays.add(url)
                }
            }
        }
    }

    fun getPreferredRelays(pubKey: String): List<String>? = userRelayLists[pubKey]

    suspend fun subscribe(subscriptionId: String, vararg filters: JsonObject) {
        val filterList = filters.toList()
        activeSubscriptions[subscriptionId] = filterList
        
        activeSessions.values.forEach { sendSubscriptionRequest(it, subscriptionId, filterList) }
    }

    private suspend fun sendSubscriptionRequest(session: DefaultClientWebSocketSession, id: String, filters: List<JsonObject>) {
        val request = buildJsonArray {
            add("REQ")
            add(id)
            filters.forEach { add(it) }
        }.toString()
        try {
            session.send(Frame.Text(request))
        } catch (ignore: Exception) {}
    }

    suspend fun publish(event: CivicEvent, preferredRelays: List<String>? = null) {
        val request = buildJsonArray {
            add("EVENT")
            add(json.encodeToJsonElement(event))
        }.toString()
        
        // 1. Primary broadcast to active sessions
        activeSessions.values.forEach { it.send(Frame.Text(request)) }
        
        // 2. Broad broadcast to the rest of the pool (Rotating subset to avoid too many conns)
        val targets = if (preferredRelays != null) {
            preferredRelays.filter { !activeSessions.containsKey(it) }
        } else {
            broadcastPool.filter { !activeSessions.containsKey(it) }.shuffled().take(15)
        }

        targets.forEach { url ->
            scope.launch {
                try {
                    client.webSocket(url) {
                        send(Frame.Text(request))
                        // Wait briefly for OK then close to save resources
                        withTimeoutOrNull(3000) {
                            for (frame in incoming) {
                                if (frame is Frame.Text && frame.readText().contains("OK")) break
                            }
                        }
                    }
                } catch (ignore: Exception) {}
            }
        }
    }
}
