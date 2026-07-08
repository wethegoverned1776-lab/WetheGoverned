package net.wetheGoverned.data

import kotlinx.serialization.json.Json
import net.wetheGoverned.core.*
import net.wetheGoverned.model.*
import net.wetheGoverned.session.PendingEventQueue
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.zk.ZkProver

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
        // 1. ZK-Proof Logic for sensitive events
        val finalTags = if (kind == CivicEventKind.POLL_VOTE) {
            val proofResult = zkProver.generateProof(
                circuitName = "voter_nostr",
                inputs = mapOf("nostrPubKey" to pubKey, "secret" to "STUB_SECRET")
            )
            tags.toMutableList().apply {
                add("zk_proof:${proofResult.proof.joinToString(",")}")
                add("nullifier:${proofResult.publicSignals[0]}")
            }
        } else {
            tags
        }

        // 2. Build Nostr Event (Stub signing for working model)
        val event = CivicEvent(
            id = randomUUID(),
            pubKey = pubKey,
            createdAt = System.currentTimeMillis() / 1000,
            kind = kind,
            tags = finalTags,
            content = content,
            sig = "STUB_SIGNATURE"
        )

        // 3. Publish and Queue
        relayManager.publish(event)
        
        // Enqueue locally for offline support / retry
        pendingQueue.enqueue(kind, content, event.sig)
    }
}
