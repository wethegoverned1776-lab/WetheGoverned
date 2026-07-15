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

        val event = CivicEvent(
            id = randomUUID(),
            pubKey = pubKey,
            createdAt = Clock.System.now().toEpochMilliseconds() / 1000,
            kind = kind,
            tags = finalTags,
            content = content,
            sig = "STUB_SIGNATURE"
        )

        relayManager.publish(event)
        pendingQueue.enqueue(kind, content, event.sig)
    }
}
