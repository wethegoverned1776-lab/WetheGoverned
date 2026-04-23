package net.wetheGoverned.repository

import kotlinx.coroutines.flow.Flow
import net.wetheGoverned.model.*

interface PollRepository {
    fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>>
    suspend fun getPoll(pollId: String): Result<CivicPoll>
    suspend fun createPoll(
        districtId: String,
        question: String,
        options: List<String>,
        closesAt: Long?,
        scope: PollScope,
        localId: String? = null
    ): Result<CivicPoll>
    suspend fun vote(pollId: String, optionId: String, voterPubKey: String): Result<Unit>
    suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String): Result<Unit>
    fun observePollPosts(pollId: String): Flow<List<PollPost>>
    fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPost>>
    fun observeThreadedPosts(parentPostId: String): Flow<List<PollPost>>
    suspend fun createPost(pollId: String, optionId: String, authorName: String, content: String, headline: String? = null, parentPostId: String? = null): Result<PollPost>
    suspend fun voteOnPost(postId: String, delta: Int): Result<Unit>
    suspend fun getPost(postId: String): Result<PollPost>
}

interface ResidentRepository {
    fun observeProfile(pubKey: String): Flow<ResidentProfile?>
    fun observeProfileByFingerprint(fingerprint: String): Flow<ResidentProfile?>
    suspend fun getProfile(pubKey: String): Result<ResidentProfile>
    suspend fun upgradeTier(pubKey: String, newTier: VerificationTier, proofToken: String): Result<ResidentProfile>
    suspend fun upgradeTierWithFingerprint(pubKey: String, newTier: VerificationTier, proofToken: String, fingerprint: String): Result<ResidentProfile>
    
    suspend fun upgradeTierFull(
        pubKey: String,
        newTier: VerificationTier,
        firstName: String,
        lastName: String,
        fingerprint: String,
        verifiedBy: String? = null // Made optional
    ): Result<Unit>

    suspend fun updateProfile(pubKey: String, displayName: String, bio: String?, avatarUrl: String?): Result<ResidentProfile>
    suspend fun getResidentCountAtAddress(fingerprint: String): Int 
    suspend fun getVouchCount(notaryPubKey: String): Int
}

interface ScorecardRepository {
    fun observeScorecard(districtId: String): Flow<RepresentativeScorecard?>
    suspend fun getScorecard(districtId: String): Result<RepresentativeScorecard>
    suspend fun submitMetricReport(districtId: String, category: String, name: String, value: String, unit: String, reporterPubKey: String): Result<DistrictMetric>
}

interface ManifestoRepository {
    fun observeManifestos(districtId: String): Flow<List<CandidateManifesto>>
    suspend fun getManifesto(manifestoId: String): Result<CandidateManifesto>
    suspend fun publishManifesto(districtId: String, title: String, body: String, candidatePubKey: String): Result<CandidateManifesto>
    suspend fun askQuestion(manifestoId: String, questionText: String, askerPubKey: String): Result<ManifestoQuestion>
    suspend fun answerQuestion(manifestoId: String, questionId: String, answerText: String, candidatePubKey: String): Result<ManifestoQuestion>
}

interface DistrictRepository {
    fun observeDistrict(districtId: String): Flow<District?>
    suspend fun getDistrict(districtId: String): Result<District>
    fun observeMetrics(districtId: String): Flow<List<DistrictMetric>>
    suspend fun refreshMetrics(districtId: String): Result<List<DistrictMetric>>
}
