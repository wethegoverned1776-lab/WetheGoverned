package net.wetheGoverned.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CivicEvent(
    val id: String,
    @SerialName("pubkey") val pubKey: String,
    @SerialName("created_at") val createdAt: Long,
    val kind: Int,
    val tags: List<String> = emptyList(),
    val content: String,
    val sig: String,
)

object CivicEventKind {
    const val DISTRICT_POLL          = 30_100
    const val LOCAL_POLL             = 30_102
    const val POLL_VOTE              = 30_101
    const val REPRESENTATIVE_SCORE   = 30_200
    const val MANIFESTO              = 30_300
    const val METRIC_REPORT          = 30_400
    const val RESIDENT_PROFILE       = 30_500
}

@Serializable
data class District(
    val id: String,
    val state: String,
    val districtNumber: Int,
    val displayName: String,
    val representativeId: String? = null,
)

enum class VerificationTier {
    UNVERIFIED, TIER_1, TIER_2, TIER_3,
}

@Serializable
data class ResidentProfile(
    val pubKey: String,
    val displayName: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val districtId: String?,
    val localId: String? = null,
    val tier: VerificationTier,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val joinedAt: Long,
    val addressFingerprint: String? = null,
    val verifiedByPubKey: String? = null // For Option B: P2P Notarization
)

// Restore alias to fix compilation in other files
typealias DistrictPoll = CivicPoll

enum class PollStatus { ACTIVE, CLOSED, ARCHIVED }
enum class PollScope { DISTRICT, LOCAL }

@Serializable
data class CivicPoll(
    val id: String,
    val scope: PollScope,
    val districtId: String,
    val localId: String? = null,
    val authorPubKey: String,
    val question: String,
    val options: List<PollOption>,
    val status: PollStatus,
    val createdAt: Long,
    val closesAt: Long?,
    val totalVotes: Int,
    val importanceScore: Int = 0,
    val userImportanceVote: Int = 0,
    val residentVoteOption: String? = null,
    val linkedLegislationId: String? = null,
)

@Serializable
data class PollOption(
    val id: String,
    val label: String,
    val voteCount: Int,
    val percentageOfTotal: Float,
)

@Serializable
data class RepresentativeScorecard(
    val representativePubKey: String,
    val districtId: String,
    val name: String,
    val party: String,
    val overallScore: Int,
    val categories: List<ScorecardCategory>,
    val lastUpdated: Long,
)

@Serializable
data class ScorecardCategory(
    val name: String,
    val officialValue: String,
    val residentReportedValue: String?,
    val score: Int,
)

@Serializable
data class CandidateManifesto(
    val id: String,
    val candidatePubKey: String,
    val districtId: String,
    val title: String,
    val body: String,
    val publishedAt: Long,
    val questions: List<ManifestoQuestion>,
)

@Serializable
data class ManifestoQuestion(
    val id: String,
    val askerPubKey: String,
    val text: String,
    val askedAt: Long,
    val answer: String? = null,
    val answeredAt: Long? = null,
)

enum class MetricSource { OFFICIAL, RESIDENT_REPORTED }

@Serializable
data class DistrictMetric(
    val id: String,
    val districtId: String,
    val category: String,
    val name: String,
    val officialValue: String,
    val residentValue: String?,
    val unit: String,
    val source: MetricSource,
    val reportedAt: Long,
    val reporterPubKey: String?,
)
