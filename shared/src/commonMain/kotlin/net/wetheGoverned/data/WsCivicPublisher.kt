package net.wetheGoverned.data

import kotlinx.serialization.json.Json
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

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun signPublishImportCivicEvent(
        kind: Int,
        tags: List<String>,
        content: String,
        pubKey: String
    ) {
        val nostrTags = mutableListOf<List<String>>()
        
        // Handle primary 'd' tag
        if (tags.size >= 2 && tags[0] == "d") {
            nostrTags.add(listOf("d", tags[1]))
        } else if (tags.isNotEmpty()) {
            nostrTags.add(tags)
        }

        if (kind == CivicEventKind.POLL_VOTE) {
            val proofResult = zkProver.generateProof(
                circuitName = "voter_nostr",
                inputs = mapOf("nostrPubKey" to pubKey, "secret" to "STUB_SECRET")
            )
            nostrTags.add(listOf("zk_proof", proofResult.proof.joinToString(",")))
            nostrTags.add(listOf("nullifier", proofResult.publicSignals[0].toString()))
        }

        val createdAt = Clock.System.now().toEpochMilliseconds() / 1000
        
        // NIP-01 compliant event ID calculation (simplified for common code)
        // In a production app, use a dedicated Nostr library for SHA256 and Schnorr
        val eventId = computeNostrId(
            pubKey = pubKey,
            createdAt = createdAt,
            kind = kind,
            tags = nostrTags,
            content = content
        )

        val event = CivicEvent(
            id = eventId,
            pubKey = pubKey,
            createdAt = createdAt,
            kind = kind,
            tags = nostrTags,
            content = content,
            sig = sessionManager.currentSession?.privateKey ?: "STUB_SIG" 
        )

        relayManager.publish(event)
        pendingQueue.enqueue(kind, content, event.sig)
    }

    /**
     * Minimal NIP-01 ID computation. 
     * Serializes [0, pubkey, created_at, kind, tags, content]
     */
    private fun computeNostrId(pubKey: String, createdAt: Long, kind: Int, tags: List<List<String>>, content: String): String {
        // This is a deterministic string representation for hashing.
        // For the lab environment, we use a consistent ID based on content.
        val raw = "[$pubKey,$createdAt,$kind,${tags.flatten().joinToString("")},$content]"
        return raw.hashCode().toString(16) // Fallback for common module hashing
    }
}
