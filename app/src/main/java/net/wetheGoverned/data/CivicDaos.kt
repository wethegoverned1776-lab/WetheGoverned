package net.wetheGoverned.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.wetheGoverned.local.entity.*

// ─────────────────────────────────────────────────────────────────────────────
// DAOs replace Primal's PostDao, ProfileDao, FeedPostDao, etc.
// The Room + Flow pattern is preserved; column names change to civic schema.
// ─────────────────────────────────────────────────────────────────────────────

@Dao
interface DistrictDao {
    @Query("SELECT * FROM districts WHERE id = :districtId")
    fun observeDistrict(districtId: String): Flow<DistrictEntity?>

    @Query("SELECT * FROM districts WHERE id = :districtId")
    suspend fun getDistrict(districtId: String): DistrictEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDistrict(district: DistrictEntity)
}

@Dao
interface ResidentProfileDao {
    @Query("SELECT * FROM resident_profiles WHERE pubKey = :pubKey")
    fun observeProfile(pubKey: String): Flow<ResidentProfileEntity?>

    @Query("SELECT * FROM resident_profiles WHERE pubKey = :pubKey")
    suspend fun getProfile(pubKey: String): ResidentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: ResidentProfileEntity)

    @Query("UPDATE resident_profiles SET tier = :tier WHERE pubKey = :pubKey")
    suspend fun updateTier(pubKey: String, tier: String)
}

@Dao
interface PollDao {
    /** Live feed of active + closed polls for a district, newest first. */
    @Query("""
        SELECT * FROM district_polls
        WHERE districtId = :districtId
        ORDER BY createdAt DESC
    """)
    fun observePolls(districtId: String): Flow<List<DistrictPollEntity>>

    @Query("SELECT * FROM district_polls WHERE id = :pollId")
    suspend fun getPoll(pollId: String): DistrictPollEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPoll(poll: DistrictPollEntity)

    /** Optimistic vote update – mirrors Primal's likeEvent local DB write. */
    @Query("""
        UPDATE district_polls
        SET residentVoteOption = :optionId,
            totalVotes = totalVotes + 1
        WHERE id = :pollId
    """)
    suspend fun applyOptimisticVote(pollId: String, optionId: String)

    /** Revert optimistic vote if relay publish fails. */
    @Query("""
        UPDATE district_polls
        SET residentVoteOption = NULL,
            totalVotes = MAX(0, totalVotes - 1)
        WHERE id = :pollId
    """)
    suspend fun revertOptimisticVote(pollId: String)

    @Query("DELETE FROM district_polls WHERE districtId = :districtId AND cachedAt < :before")
    suspend fun evictStalePolls(districtId: String, before: Long)
}

@Dao
interface ScorecardDao {
    @Query("SELECT * FROM representative_scorecards WHERE districtId = :districtId")
    fun observeScorecard(districtId: String): Flow<RepresentativeScorecardEntity?>

    @Query("SELECT * FROM representative_scorecards WHERE districtId = :districtId")
    suspend fun getScorecard(districtId: String): RepresentativeScorecardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertScorecard(scorecard: RepresentativeScorecardEntity)

    @Query("SELECT * FROM scorecard_categories WHERE districtId = :districtId")
    suspend fun getCategoriesForDistrict(districtId: String): List<ScorecardCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategories(categories: List<ScorecardCategoryEntity>)

    @Query("DELETE FROM scorecard_categories WHERE districtId = :districtId")
    suspend fun deleteCategoriesForDistrict(districtId: String)
}

@Dao
interface ManifestoDao {
    @Query("""
        SELECT * FROM candidate_manifestos
        WHERE districtId = :districtId
        ORDER BY publishedAt DESC
    """)
    fun observeManifestos(districtId: String): Flow<List<CandidateManifestoEntity>>

    @Query("SELECT * FROM candidate_manifestos WHERE id = :manifestoId")
    suspend fun getManifesto(manifestoId: String): CandidateManifestoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertManifesto(manifesto: CandidateManifestoEntity)

    @Query("SELECT * FROM manifesto_questions WHERE manifestoId = :manifestoId ORDER BY askedAt ASC")
    suspend fun getQuestions(manifestoId: String): List<ManifestoQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuestion(question: ManifestoQuestionEntity)

    @Query("UPDATE manifesto_questions SET answer = :answer, answeredAt = :answeredAt WHERE id = :questionId")
    suspend fun recordAnswer(questionId: String, answer: String, answeredAt: Long)
}

@Dao
interface MetricDao {
    @Query("""
        SELECT * FROM district_metrics
        WHERE districtId = :districtId
        ORDER BY reportedAt DESC
    """)
    fun observeMetrics(districtId: String): Flow<List<DistrictMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMetric(metric: DistrictMetricEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMetrics(metrics: List<DistrictMetricEntity>)

    @Query("DELETE FROM district_metrics WHERE districtId = :districtId AND cachedAt < :before")
    suspend fun evictStaleMetrics(districtId: String, before: Long)
}

@Dao
interface PendingEventDao {
    @Query("SELECT * FROM pending_civic_events ORDER BY createdAt ASC")
    suspend fun getAllPending(): List<net.wetheGoverned.local.entity.PendingCivicEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(event: net.wetheGoverned.local.entity.PendingCivicEventEntity)

    @Query("DELETE FROM pending_civic_events WHERE eventId = :eventId")
    suspend fun dequeue(eventId: String)

    @Query("UPDATE pending_civic_events SET retryCount = retryCount + 1 WHERE eventId = :eventId")
    suspend fun incrementRetry(eventId: String)
}
