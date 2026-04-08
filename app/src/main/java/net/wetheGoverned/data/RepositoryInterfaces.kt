package net.wetheGoverned.repository

import kotlinx.coroutines.flow.Flow
import net.wetheGoverned.model.*

// ─────────────────────────────────────────────────────────────────────────────
// These interfaces replace Primal's FeedRepository, ProfileRepository, etc.
// Concrete implementations live in the :data module, exactly as in Primal.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Replaces FeedRepository.
 * Provides district‑scoped polls instead of a social feed.
 */
interface PollRepository {
    /** Live stream of active polls for a district, newest first. */
    fun observeDistrictPolls(districtId: String): Flow<List<DistrictPoll>>

    /** Fetch a single poll by id (remote + cache). */
    suspend fun getPoll(pollId: String): Result<DistrictPoll>

    /** Create a new poll (requires Tier 2+ resident). */
    suspend fun createPoll(
        districtId: String,
        question: String,
        options: List<String>,
        closesAt: Long?,
    ): Result<DistrictPoll>

    /**
     * Submit a vote.
     * Internally publishes a signed CivicEvent of kind POLL_VOTE to the relay.
     * Performs optimistic local update identical to Primal's likeEvent pattern.
     */
    suspend fun vote(
        pollId: String,
        optionId: String,
        voterPubKey: String,
    ): Result<Unit>
}

/**
 * Replaces Primal's ProfileRepository.
 * Manages verified resident profiles.
 */
interface ResidentRepository {
    fun observeProfile(pubKey: String): Flow<ResidentProfile?>

    suspend fun getProfile(pubKey: String): Result<ResidentProfile>

    /** Upgrade a resident's verification tier after backend check. */
    suspend fun upgradeTier(
        pubKey: String,
        newTier: VerificationTier,
        proofToken: String,
    ): Result<ResidentProfile>

    suspend fun updateProfile(
        pubKey: String,
        displayName: String,
        bio: String?,
        avatarUrl: String?,
    ): Result<ResidentProfile>
}

/**
 * New – no Primal equivalent.
 * Representative scorecards sourced from official data + resident reports.
 */
interface ScorecardRepository {
    fun observeScorecard(districtId: String): Flow<RepresentativeScorecard?>

    suspend fun getScorecard(districtId: String): Result<RepresentativeScorecard>

    /**
     * Submit a resident metric report.
     * Publishes a signed CivicEvent of kind METRIC_REPORT.
     * Requires Tier 3.
     */
    suspend fun submitMetricReport(
        districtId: String,
        category: String,
        name: String,
        value: String,
        unit: String,
        reporterPubKey: String,
    ): Result<DistrictMetric>
}

/**
 * Replaces Primal's long‑form article / reads repository.
 * Manages candidate manifestos and resident Q&A threads.
 */
interface ManifestoRepository {
    fun observeManifestos(districtId: String): Flow<List<CandidateManifesto>>

    suspend fun getManifesto(manifestoId: String): Result<CandidateManifesto>

    suspend fun publishManifesto(
        districtId: String,
        title: String,
        body: String,
        candidatePubKey: String,
    ): Result<CandidateManifesto>

    suspend fun askQuestion(
        manifestoId: String,
        questionText: String,
        askerPubKey: String,
    ): Result<ManifestoQuestion>

    suspend fun answerQuestion(
        manifestoId: String,
        questionId: String,
        answerText: String,
        candidatePubKey: String,
    ): Result<ManifestoQuestion>
}

/**
 * District data – geography, representative info, metric feed.
 */
interface DistrictRepository {
    fun observeDistrict(districtId: String): Flow<District?>

    suspend fun getDistrict(districtId: String): Result<District>

    fun observeMetrics(districtId: String): Flow<List<DistrictMetric>>

    suspend fun refreshMetrics(districtId: String): Result<List<DistrictMetric>>
}
