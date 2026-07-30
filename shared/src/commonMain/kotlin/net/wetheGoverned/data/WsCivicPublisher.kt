package net.wetheGoverned.data

import kotlinx.serialization.json.*
import net.wetheGoverned.core.*
import net.wetheGoverned.model.*
import net.wetheGoverned.session.PendingEventQueue
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.zk.ZkProver
import kotlinx.datetime.Clock

class WsCivicPublisher(
    private val relayManager: NostrRelayManager,
    private val sessionManager: SessionManager,
    private val pendingQueue: PendingEventQueue,
    private val zkProver: ZkProver,
) : CivicPublisher {

    private val json = CivicJson

    override suspend fun signPublishImportCivicEvent(
        kind: Int,
        tags: List<List<String>>,
        content: String,
        pubKey: String
    ) {
        val nostrTags = tags.toMutableList()
        
        // Ensure geography tags are lowercase and consistent
        val normalizedTags = nostrTags.map { tag ->
            tag.mapIndexed { index, value -> if (index == 1) value.lowercase() else value }
        }
        
        val createdAt = Clock.System.now().toEpochMilliseconds() / 1000
        
        // Step 1: Compute Canonical ID
        val eventId = computeNostrId(
            pubKey = pubKey,
            createdAt = createdAt,
            kind = kind,
            tags = normalizedTags,
            content = content
        )

        // Step 2: Protocol-compliant BIP-340 Schnorr signature (128 hex chars)
        val privateKey = sessionManager.currentSession?.privateKey 
            ?: "0000000000000000000000000000000000000000000000000000000000000001"
        
        val signature = Secp256k1KeyManager.sign(eventId, privateKey)
        
        val event = CivicEvent(
            id = eventId,
            pubKey = pubKey,
            createdAt = createdAt,
            kind = kind,
            tags = normalizedTags,
            content = content,
            sig = signature
        )

        println("📤 Publishing to Mesh: ${event.id}")

        // Step 3: Broadcast
        val isCritical = kind in listOf(
            CivicEventKind.FEDERAL_POLL, CivicEventKind.STATE_POLL, 
            CivicEventKind.DISTRICT_POLL, CivicEventKind.LOCAL_POLL, 
            CivicEventKind.POLL_VOTE, CivicEventKind.IMPORTANCE_VOTE
        )

        val preferred = if (!isCritical) relayManager.getPreferredRelays(pubKey) else null
        relayManager.publish(event, preferred)

        pendingQueue.enqueue(kind, content, event.sig)
    }

    /**
     * NIP-01 Canonical ID computation. 
     * Serializes using shared CivicJson to ensure cross-platform character-matching.
     */
    private fun computeNostrId(pubKey: String, createdAt: Long, kind: Int, tags: List<List<String>>, content: String): String {
        val jsonArray = buildJsonArray {
            add(JsonPrimitive(0))
            add(JsonPrimitive(pubKey))
            add(JsonPrimitive(createdAt))
            add(JsonPrimitive(kind))
            add(buildJsonArray {
                tags.forEach { tag ->
                    add(buildJsonArray {
                        tag.forEach { element -> add(JsonPrimitive(element)) }
                    })
                }
            })
            add(JsonPrimitive(content))
        }
        
        // CRITICAL: Must use unified CivicJson for encoding to ensure no spaces/formatting differences
        val serialized = CivicJson.encodeToString(JsonArray.serializer(), jsonArray)
        return computeSha256(serialized)
    }
}
