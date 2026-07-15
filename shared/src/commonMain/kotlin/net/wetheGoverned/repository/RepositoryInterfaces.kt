package net.wetheGoverned.repository

import kotlinx.coroutines.flow.Flow
import net.wetheGoverned.model.*

interface PollRepository {
    fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>>
    fun observePollsByIds(districtIds: List<String>): Flow<List<CivicPoll>>
    suspend fun getPoll(pollId: String): Result<CivicPoll>
    suspend fun createPoll(
        districtId: String,
        question: String,
        options: List<String>,
        closesAt: Long?,
        scope: CivicScope,
        localId: String? = null
    ): Result<CivicPoll>
    suspend fun vote(pollId: String, optionId: String, voterPubKey: String): Result<Unit>
    suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String): Result<Unit>
    
    // Scaling Fix: Paging for large datasets
    fun observePollsPaged(districtId: String, limit: Int, offset: Int): Flow<List<CivicPoll>>
    
    // ERR_V4_03 FIX: Scoped observation to offload filtering from ViewModel
    fun observePollsByScope(scope: CivicScope, districtId: String): Flow<List<CivicPoll>>

    fun observePollPosts(pollId: String): Flow<List<PollPost>>
    fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPost>>
    fun observeThreadedPosts(parentPostId: String): Flow<List<PollPost>>
    suspend fun createPost(pollId: String, optionId: String, authorName: String, content: String, headline: String? = null, parentPostId: String? = null): Result<PollPost>
    suspend fun voteOnPost(postId: String, delta: Int): Result<Unit>
    suspend fun getPost(postId: String): Result<PollPost>

    // Mesh Syncing
    suspend fun getAllPolls(): List<CivicPoll>
    suspend fun getPollsForJurisdictions(jurisdictionIds: List<String>, since: Long): List<CivicPoll>
    suspend fun syncPoll(poll: CivicPoll)
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
        fingerprint: String,
        verifiedBy: String? = null
    ): Result<Unit>

    suspend fun updateProfile(pubKey: String, displayName: String, avatarUrl: String?): Result<ResidentProfile>
    suspend fun updateDistrict(pubKey: String, districtId: String): Result<Unit>
    suspend fun getResidentCountAtAddress(fingerprint: String): Int 
    suspend fun getVouchCount(notaryPubKey: String): Int
    fun observeProfilesVerifiedBy(verifierPubKey: String): Flow<List<ResidentProfile>>
    suspend fun createProfile(profile: ResidentProfile)
}

interface VoteRepository {
    fun observeAllVotes(): Flow<List<CivicVote>>
    fun observeVotesByUser(pubKey: String): Flow<List<CivicVote>>
    suspend fun flagVote(voteId: String, reason: String, expiresAt: Long): Result<Unit>
    suspend fun disputeVote(voteId: String, comment: String): Result<Unit>
    suspend fun resolveVote(voteId: String): Result<Unit>
    
    // Mesh Syncing
    suspend fun syncVote(vote: CivicVote)
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

interface CommunityRepository {
    fun observePosts(districtId: String, kind: CommunityPostKind? = null): Flow<List<CommunityPost>>
    suspend fun getPost(postId: String): Result<CommunityPost>
    suspend fun createPost(
        districtId: String,
        authorPubKey: String,
        kind: CommunityPostKind,
        title: String,
        description: String,
        price: Double? = null,
        location: String? = null,
        contactInfo: String? = null
    ): Result<CommunityPost>
    suspend fun deletePost(postId: String): Result<Unit>
    
    // Mesh Syncing
    suspend fun getAllPosts(): List<CommunityPost>
    suspend fun syncPost(post: CommunityPost)
}

interface VerificationRequestRepository {
    fun observeRequestsForDistrict(districtId: String): Flow<List<VerificationRequest>>
    fun observeRequestsForState(stateId: String): Flow<List<VerificationRequest>>
    suspend fun createRequest(request: VerificationRequest): Result<Unit>
    suspend fun updateRequestStatus(requestId: String, status: VerificationRequestStatus, handledBy: String): Result<Unit>
    suspend fun getRequest(requestId: String): Result<VerificationRequest>
}
