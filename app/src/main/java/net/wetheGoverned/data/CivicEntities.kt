package net.wetheGoverned.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ─────────────────────────────────────────────────────────────────────────────
// Room @Entity classes replace Primal's PostData, ProfileData, etc.
// The Room + DAO pattern from Primal is preserved exactly; only the schema
// columns change to reflect civic domain objects.
// ─────────────────────────────────────────────────────────────────────────────

@Entity(tableName = "districts")
data class DistrictEntity(
    @PrimaryKey val id: String,              // "us-fl-06"
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
    val districtId: String,
    val tier: String,                        // VerificationTier.name()
    val avatarUrl: String?,
    val bio: String?,
    val joinedAt: Long,
    val cachedAt: Long,
)

/**
 * Flattened poll row.  Options are stored as a JSON string (via TypeConverter)
 * to avoid a separate join table for this simple case.
 */
@Entity(tableName = "district_polls")
data class DistrictPollEntity(
    @PrimaryKey val id: String,
    val districtId: String,
    val authorPubKey: String,
    val question: String,
    val optionsJson: String,                 // JSON array of PollOptionEntity
    val status: String,                      // PollStatus.name()
    val createdAt: Long,
    val closesAt: Long?,
    val totalVotes: Int,
    val residentVoteOption: String?,
    val cachedAt: Long,
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
    val source: String,                      // MetricSource.name()
    val reportedAt: Long,
    val reporterPubKey: String?,
    val cachedAt: Long,
)

/**
 * Tracks locally‑signed but not‑yet‑confirmed CivicEvents.
 * Mirrors Primal's unpublished event queue pattern.
 */
@Entity(tableName = "pending_civic_events")
data class PendingCivicEventEntity(
    @PrimaryKey val eventId: String,
    val kind: Int,
    val contentJson: String,
    val sig: String,
    val createdAt: Long,
    val retryCount: Int = 0,
)
