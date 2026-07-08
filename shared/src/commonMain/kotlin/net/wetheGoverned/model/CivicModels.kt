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
    const val FEDERAL_POLL           = 30_098
    const val STATE_POLL             = 30_099
    const val DISTRICT_POLL          = 30_100
    const val LOCAL_POLL             = 30_102
    const val POLL_VOTE              = 30_101
    const val REPRESENTATIVE_SCORE   = 30_200
    const val MANIFESTO              = 30_300
    const val METRIC_REPORT          = 30_400
    const val RESIDENT_PROFILE       = 30_500
    const val COMMUNITY_POST         = 30_600
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
data class CivicVote(
    val id: String,
    val pollId: String,
    val voterPubKey: String,
    val voterName: String,
    val optionId: String,
    val timestamp: Long,
    val nonce: Long,
    val signature: String? = null,
    val isFlagged: Boolean = false,
    val flagReason: String? = null,
    val disputeComment: String? = null,
    val disputeExpiresAt: Long? = null,
    val status: ConflictStatus = ConflictStatus.NONE
)

enum class ConflictStatus { NONE, FLAGGED, DISPUTED, RESOLVED }

enum class PollStatus { ACTIVE, CLOSED, ARCHIVED }
enum class PollScope { FEDERAL, STATE, DISTRICT, LOCAL }
typealias CivicScope = PollScope

@Serializable
data class RepresentativeScorecard(
    val representativePubKey: String,
    val districtId: String,
    val scope: CivicScope = CivicScope.DISTRICT,
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
    val scope: CivicScope = CivicScope.DISTRICT,
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

@Serializable
data class CommunityPost(
    val id: String,
    val authorPubKey: String,
    val districtId: String,
    val kind: CommunityPostKind,
    val title: String,
    val description: String,
    val price: Double? = null,
    val location: String? = null,
    val contactInfo: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val tags: List<String> = emptyList()
)

enum class CommunityPostKind {
    MARKETPLACE, WORKSHOP, CLASS, JOB, GENERAL
}

@Serializable
data class AddressResolution(
    val address: String,
    val federalDistrict: District? = null,
    val stateUpperDistrict: District? = null,
    val stateLowerDistrict: District? = null,
    val localJurisdiction: String? = null,
    val sources: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
