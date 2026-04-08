package net.wetheGoverned.core

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

// ─────────────────────────────────────────────────────────────────────────────
// DispatcherProvider – unchanged from Primal; allows easy test injection
// ─────────────────────────────────────────────────────────────────────────────

interface DispatcherProvider {
    fun io(): CoroutineContext
    fun main(): CoroutineContext
    fun default(): CoroutineContext
}

class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override fun io() = Dispatchers.IO
    override fun main() = Dispatchers.Main
    override fun default() = Dispatchers.Default
}

// ─────────────────────────────────────────────────────────────────────────────
// CivicPublisher  (replaces PrimalPublisher / NostrPublisher)
//
// Responsibilities (same as the original):
//   1. Sign a CivicEvent with the resident's private key (NIP-01)
//   2. Broadcast to the WeTheGoverned relay (wss://relay.wetheGoverned.net)
//   3. Import the event back into the local Room cache
//
// The WebSocket / Ktor networking from Primal's core:networking-primal module
// is reused without modification – only the relay URL and event kind set change.
// ─────────────────────────────────────────────────────────────────────────────

class CivicPublishException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

/**
 * Interface mirrors PrimalPublisher so the existing signing + relay infra
 * from Primal's core module can be dropped in as the implementation.
 */
interface CivicPublisher {
    /**
     * Sign the event with the resident's key, publish it to the relay,
     * and persist the confirmed event locally.
     *
     * @throws CivicPublishException if signing or relay broadcast fails.
     */
    suspend fun signPublishImportCivicEvent(
        kind: Int,
        tags: List<String>,
        content: String,
        pubKey: String,
    )
}

/**
 * WsCivicPublisher – concrete implementation backed by Primal's existing
 * PrimalApiClient WebSocket layer.
 *
 * The relay is configurable so the app can target:
 *   - wss://relay.wetheGoverned.net  (production)
 *   - wss://relay.damus.io           (fallback / dev testing)
 */
class WsCivicPublisher(
    private val relayUrl: String = "wss://relay.wetheGoverned.net",
    private val apiClient: Any,   // PrimalApiClient from :core – injected via DI
    private val dispatchers: DispatcherProvider,
) : CivicPublisher {

    override suspend fun signPublishImportCivicEvent(
        kind: Int,
        tags: List<String>,
        content: String,
        pubKey: String,
    ) {
        // Implementation delegates to the existing PrimalApiClient WebSocket
        // infrastructure; only the relay URL and kind are swapped.
        //
        // Pseudocode (real impl uses the Ktor WS client from :core):
        //
        //   val unsignedEvent = buildUnsignedEvent(kind, tags, content, pubKey)
        //   val signedEvent   = keyStore.sign(unsignedEvent)
        //   val response      = apiClient.publishEvent(relayUrl, signedEvent)
        //   if (!response.isOk) throw CivicPublishException(response.reason)
        //   localCache.importEvent(signedEvent)

        TODO("Delegate to PrimalApiClient.publishEvent() with civic relay URL")
    }
}
