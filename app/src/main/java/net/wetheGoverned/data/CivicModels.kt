package net.wetheGoverned.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────────────────────────────────────
// CORE EVENT  (replaces NostrEvent)
// All civic actions are still published as signed Nostr events to the relay
// network, but the domain objects here model the WeTheGoverned semantic layer.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A signed Nostr event that carries WeTheGoverned civic payload.
 * Mirrors the original NostrEvent shape so the existing core networking &
 * signing infrastructure can be reused without modification.
 */
@Serializable
data class CivicEvent(
    val id: String,
    @SerialName("pubkey") val pubKey: String,
    @SerialName("created_at") val createdAt: Long,
    val kind: Int,           // See CivicEventKind for WeTheGoverned‑specific kinds
    val tags: List<String> = emptyList(),
    val content: String,
    val sig: String,
)

/**
 * NIP-78 / custom kind assignments for WeTheGoverned.
 * 30_000–39_999 are parameterised replaceable events (per Nostr spec).
 */
object CivicEventKind {
    const val DISTRICT_POLL          = 30_100  // a live poll scoped to a district
    const val POLL_VOTE              = 30_101  // a resident's vote on a poll
    const val REPRESENTATIVE_SCORE   = 30_200  // scorecard entry for a rep
    const val MANIFESTO              = 30_300  // candidate manifesto / Q&A thread
    const val METRIC_REPORT          = 30_400  // resident‑reported district metric
    const val RESIDENT_PROFILE       = 30_500  // verified resident profile
}

// ─────────────────────────────────────────────────────────────────────────────
// DISTRICT
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A U.S. congressional district. The beta targets FL‑06.
 */
data class District(
    val id: String,               // e.g. "us-fl-06"
    val state: String,            // e.g. "Florida"
    val districtNumber: Int,      // e.g. 6
    val displayName: String,      // e.g. "Florida District 6"
    val representativeId: String? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// RESIDENT / USER  (replaces Primal Profile)
// ─────────────────────────────────────────────────────────────────────────────

enum class VerificationTier {
    UNVERIFIED,
    TIER_1,   // email‑verified resident
    TIER_2,   // address‑verified resident (can vote in district polls)
    TIER_3,   // identity‑verified resident (can submit metric reports)
}

data class ResidentProfile(
    val pubKey: String,
    val displayName: String,
    val districtId: String,
    val tier: VerificationTier,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val joinedAt: Long,
)

// ─────────────────────────────────────────────────────────────────────────────
// POLL  (replaces Feed / Note)
// ─────────────────────────────────────────────────────────────────────────────

enum class PollStatus { ACTIVE, CLOSED, ARCHIVED }

data class DistrictPoll(
    val id: String,
    val districtId: String,
    val authorPubKey: String,
    val question: String,
    val options: List<PollOption>,
    val status: PollStatus,
    val createdAt: Long,
    val closesAt: Long?,
    val totalVotes: Int,
    val residentVoteOption: String? = null,  // null if current user hasn't voted
)

data class PollOption(
    val id: String,
    val label: String,
    val voteCount: Int,
    val percentageOfTotal: Float,
)

// ─────────────────────────────────────────────────────────────────────────────
// REPRESENTATIVE SCORECARD  (new – no Primal equivalent)
// ─────────────────────────────────────────────────────────────────────────────

data class RepresentativeScorecard(
    val representativePubKey: String,
    val districtId: String,
    val name: String,
    val party: String,
    val overallScore: Int,               // 0–100
    val categories: List<ScorecardCategory>,
    val lastUpdated: Long,
)

data class ScorecardCategory(
    val name: String,                    // e.g. "Attendance", "Responsiveness"
    val officialValue: String,
    val residentReportedValue: String?,
    val score: Int,                      // 0–100
)

// ─────────────────────────────────────────────────────────────────────────────
// MANIFESTO / Q&A  (replaces Nostr long‑form article)
// ─────────────────────────────────────────────────────────────────────────────

data class CandidateManifesto(
    val id: String,
    val candidatePubKey: String,
    val districtId: String,
    val title: String,
    val body: String,
    val publishedAt: Long,
    val questions: List<ManifestoQuestion>,
)

data class ManifestoQuestion(
    val id: String,
    val askerPubKey: String,
    val text: String,
    val askedAt: Long,
    val answer: String? = null,
    val answeredAt: Long? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// DISTRICT METRICS  (new – no Primal equivalent)
// ─────────────────────────────────────────────────────────────────────────────

enum class MetricSource { OFFICIAL, RESIDENT_REPORTED }

data class DistrictMetric(
    val id: String,
    val districtId: String,
    val category: String,            // e.g. "Infrastructure", "Public Safety"
    val name: String,                // e.g. "Road condition rating"
    val officialValue: String,
    val residentValue: String?,
    val unit: String,
    val source: MetricSource,
    val reportedAt: Long,
    val reporterPubKey: String?,
)
