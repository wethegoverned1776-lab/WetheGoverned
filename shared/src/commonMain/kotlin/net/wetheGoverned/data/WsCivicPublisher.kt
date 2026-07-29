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
        
        // Add redundant 't' tag for district indexing robustness
        tags.find { it.getOrNull(0) == "g" }?.getOrNull(1)?.let { districtId ->
            if (nostrTags.none { it.getOrNull(0) == "t" && it.getOrNull(1) == districtId }) {
                nostrTags.add(listOf("t", districtId))
            }
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
        
        // NIP-01 compliant event ID calculation
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

        // Broad broadcast for critical governance, NIP-65 for user content
        val isCritical = kind in listOf(
            CivicEventKind.FEDERAL_POLL, CivicEventKind.STATE_POLL, 
            CivicEventKind.DISTRICT_POLL, CivicEventKind.LOCAL_POLL, 
            CivicEventKind.POLL_VOTE
        )

        val preferred = if (!isCritical) relayManager.getPreferredRelays(pubKey) else null
        relayManager.publish(event, preferred)

        pendingQueue.enqueue(kind, content, event.sig)
    }

    /**
     * NIP-01 ID computation. 
     * Serializes [0, pubkey, created_at, kind, tags, content]
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
        
        return sha256(jsonArray.toString())
    }
}
