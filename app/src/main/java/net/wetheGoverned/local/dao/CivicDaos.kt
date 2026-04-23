package net.wetheGoverned.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.wetheGoverned.local.entity.*

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

    @Query("SELECT * FROM resident_profiles WHERE addressFingerprint = :fingerprint")
    fun observeProfilesByFingerprint(fingerprint: String): Flow<List<ResidentProfileEntity>>

    @Query("SELECT COUNT(*) FROM resident_profiles WHERE addressFingerprint = :fingerprint")
    suspend fun getProfileCountByFingerprint(fingerprint: String): Int

    @Query("SELECT COUNT(*) FROM resident_profiles WHERE verifiedByPubKey = :notaryPubKey")
    suspend fun getVouchCount(notaryPubKey: String): Int

    @Query("SELECT * FROM resident_profiles")
    suspend fun getAllProfiles(): List<ResidentProfileEntity>

    @Query("SELECT * FROM resident_profiles WHERE pubKey = :pubKey")
    suspend fun getProfile(pubKey: String): ResidentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: ResidentProfileEntity)

    @Query("UPDATE resident_profiles SET tier = :tier WHERE pubKey = :pubKey")
    suspend fun updateTier(pubKey: String, tier: String)

    @Query("UPDATE resident_profiles SET tier = :tier, addressFingerprint = :fingerprint WHERE pubKey = :pubKey")
    suspend fun updateTierWithFingerprint(pubKey: String, tier: String, fingerprint: String)
}

@Dao
interface PollDao {
    @Query("SELECT * FROM district_polls WHERE districtId = :districtId ORDER BY importanceScore DESC, createdAt DESC")
    fun observePolls(districtId: String): Flow<List<DistrictPollEntity>>

    @Query("SELECT * FROM district_polls WHERE districtId = :districtId ORDER BY importanceScore DESC, createdAt DESC")
    suspend fun getPollsSync(districtId: String): List<DistrictPollEntity>

    @Query("SELECT * FROM district_polls ORDER BY importanceScore DESC, createdAt DESC")
    suspend fun getAllPollsAcrossDistricts(): List<DistrictPollEntity>

    @Query("SELECT * FROM district_polls WHERE id = :pollId")
    suspend fun getPoll(pollId: String): DistrictPollEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPoll(poll: DistrictPollEntity)

    @Query("UPDATE district_polls SET residentVoteOption = :optionId, totalVotes = totalVotes + 1 WHERE id = :pollId")
    suspend fun applyOptimisticVote(pollId: String, optionId: String)

    @Query("DELETE FROM district_polls WHERE districtId = :districtId AND cachedAt < :before")
    suspend fun evictStalePolls(districtId: String, before: Long)
}

@Dao
interface PollPostDao {
    @Query("SELECT * FROM poll_posts WHERE pollId = :pollId ORDER BY score DESC")
    fun getAllPostsForPoll(pollId: String): Flow<List<PollPostEntity>>

    @Query("SELECT * FROM poll_posts WHERE pollId = :pollId AND optionId = :optionId AND parentPostId IS NULL ORDER BY score DESC")
    fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPostEntity>>

    @Query("SELECT * FROM poll_posts WHERE parentPostId = :parentPostId ORDER BY score DESC")
    fun observeThreadedPosts(parentPostId: String): Flow<List<PollPostEntity>>

    @Query("SELECT * FROM poll_posts WHERE id = :postId")
    suspend fun getPost(postId: String): PollPostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPost(post: PollPostEntity)

    @Query("UPDATE poll_posts SET score = score + :delta, userVote = :userVote WHERE id = :postId")
    suspend fun updateVote(postId: String, delta: Int, userVote: Int)

    @Query("SELECT * FROM poll_posts")
    suspend fun getAllPosts(): List<PollPostEntity>
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
    @Query("SELECT * FROM candidate_manifestos WHERE districtId = :districtId ORDER BY publishedAt DESC")
    fun observeManifestos(districtId: String): Flow<List<CandidateManifestoEntity>>

    @Query("SELECT * FROM candidate_manifestos WHERE id = :manifestoId")
    suspend fun getManifesto(manifestoId: String): CandidateManifestoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertManifesto(manifesto: CandidateManifestoEntity)

    @Query("SELECT * FROM manifesto_questions WHERE manifestoId = :manifestoId ORDER BY askedAt ASC")
    suspend fun getQuestions(manifestoId: String): List<ManifestoQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuestion(question: ManifestoQuestionEntity)
}

@Dao
interface MetricDao {
    @Query("SELECT * FROM district_metrics WHERE districtId = :districtId ORDER BY reportedAt DESC")
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
    suspend fun getAllPending(): List<PendingCivicEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(event: PendingCivicEventEntity)

    @Query("DELETE FROM pending_civic_events WHERE eventId = :eventId")
    suspend fun dequeue(eventId: String)
}
