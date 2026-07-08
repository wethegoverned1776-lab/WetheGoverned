package net.wetheGoverned.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "districts")
data class DistrictEntity(
    @PrimaryKey val id: String,
    val state: String,
    val districtNumber: Int,
    val displayName: String,
    val representativeId: String?,
    val cachedAt: Long,
)

@Entity(tableName = "resident_profiles")
data class ResidentProfileEntity(
    @PrimaryKey val pubKey: String,
    val displayName: String,
    val districtId: String?,
    val localId: String?,
    val tier: String,
    val avatarUrl: String?,
    val joinedAt: Long,
    val addressFingerprint: String?,
    val verifiedByPubKey: String? = null,
    val cachedAt: Long,
)

@Entity(tableName = "district_polls")
data class DistrictPollEntity(
    @PrimaryKey val id: String,
    val scope: String?,
    val districtId: String,
    val localId: String?,
    val authorPubKey: String,
    val question: String,
    val optionsJson: String,
    val status: String,
    val createdAt: Long,
    val closesAt: Long?,
    val totalVotes: Int,
    val importanceScore: Int = 0,
    val userImportanceVote: Int = 0,
    val residentVoteOption: String?,
    val linkedLegislationId: String? = null,
    val districtBreakdownJson: String? = null, // Added for State Weighted Rules
    val cachedAt: Long,
)

@Entity(tableName = "poll_votes")
data class CivicVoteEntity(
    @PrimaryKey val id: String,
    val pollId: String,
    val voterPubKey: String,
    val voterName: String,
    val optionId: String,
    val timestamp: Long,
    val nonce: Long,
    val signature: String?,
    val isFlagged: Boolean = false,
    val flagReason: String? = null,
    val disputeComment: String? = null,
    val disputeExpiresAt: Long? = null,
    val status: String = "NONE"
)

@Entity(tableName = "poll_posts")
data class PollPostEntity(
    @PrimaryKey val id: String,
    val pollId: String,
    val optionId: String,
    val parentPostId: String?,
    val headline: String?,
    val authorName: String,
    val content: String,
    val score: Int,
    val userVote: Int,
    val createdAt: Long
)

@Entity(tableName = "scorecard_categories")
data class ScorecardCategoryEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
    val districtId: String,
    val categoryName: String,
    val officialValue: String,
    val residentReportedValue: String?,
    val score: Int,
)

@Entity(tableName = "representative_scorecards")
data class RepresentativeScorecardEntity(
    @PrimaryKey val districtId: String,
    val representativePubKey: String,
    val name: String,
    val party: String,
    val overallScore: Int,
    val lastUpdated: Long,
    val cachedAt: Long,
)

@Entity(tableName = "candidate_manifestos")
data class CandidateManifestoEntity(
    @PrimaryKey val id: String,
    val candidatePubKey: String,
    val districtId: String,
    val title: String,
    val body: String,
    val publishedAt: Long,
    val cachedAt: Long,
)

@Entity(tableName = "manifesto_questions")
data class ManifestoQuestionEntity(
    @PrimaryKey val id: String,
    val manifestoId: String,
    val askerPubKey: String,
    val text: String,
    val askedAt: Long,
    val answer: String?,
    val answeredAt: Long?,
)

@Entity(tableName = "district_metrics")
data class DistrictMetricEntity(
    @PrimaryKey val id: String,
    val districtId: String,
    val category: String,
    val name: String,
    val officialValue: String,
    val residentValue: String?,
    val unit: String,
    val source: String,
    val reportedAt: Long,
    val reporterPubKey: String?,
    val cachedAt: Long,
)

@Entity(tableName = "pending_civic_events")
data class PendingCivicEventEntity(
    @PrimaryKey val eventId: String,
    val kind: Int,
    val contentJson: String,
    val sig: String,
    val createdAt: Long,
    val retryCount: Int = 0,
)

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey val id: String,
    val authorPubKey: String,
    val districtId: String,
    val kind: String,
    val title: String,
    val description: String,
    val price: Double?,
    val location: String?,
    val contactInfo: String?,
    val createdAt: Long,
    val expiresAt: Long?,
    val cachedAt: Long
)
