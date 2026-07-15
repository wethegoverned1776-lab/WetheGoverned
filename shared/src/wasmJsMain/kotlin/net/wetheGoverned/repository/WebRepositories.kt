package net.wetheGoverned.repository

import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import net.wetheGoverned.model.*
import net.wetheGoverned.session.*
import net.wetheGoverned.remote.api.*
import io.ktor.client.*

// Simple LocalStorage wrapper for Wasm
private fun localStorageGet(key: String): String? = 
    js("window.localStorage.getItem(key)")

private fun localStorageSet(key: String, value: String) = 
    js("window.localStorage.setItem(key, value)")

private fun localStorageRemove(key: String) = 
    js("window.localStorage.removeItem(key)")

abstract class WebRepository(val typeName: String) {
    protected val json = Json { ignoreUnknownKeys = true }

    protected fun save(id: String, content: String) {
        localStorageSet("${typeName}_$id", content)
    }

    protected fun load(id: String): String? {
        return localStorageGet("${typeName}_$id")
    }
}

class WebVoteRepository : VoteRepository, WebRepository("votes") {
    override fun observeAllVotes(): Flow<List<CivicVote>> = flowOf(emptyList())
    override fun observeVotesByUser(pubKey: String): Flow<List<CivicVote>> = flowOf(emptyList())
    override suspend fun flagVote(voteId: String, reason: String, expiresAt: Long): Result<Unit> = Result.success(Unit)
    override suspend fun disputeVote(voteId: String, comment: String): Result<Unit> = Result.success(Unit)
    override suspend fun resolveVote(voteId: String): Result<Unit> = Result.success(Unit)
    override suspend fun syncVote(vote: CivicVote) { save(vote.id, json.encodeToString(CivicVote.serializer(), vote)) }
}

class WebPollRepository : PollRepository, WebRepository("polls") {
    private val _polls = MutableStateFlow<List<CivicPoll>>(emptyList())
    
    override fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>> = _polls.map { list -> list.filter { it.districtId == districtId || it.districtId == "us" } }
    override fun observePollsByIds(districtIds: List<String>): Flow<List<CivicPoll>> = _polls.map { list -> list.filter { it.districtId in districtIds } }
    override fun observePollsByScope(scope: PollScope, districtId: String): Flow<List<CivicPoll>> = _polls.map { list -> list.filter { it.scope == scope } }
    override suspend fun getPoll(pollId: String): Result<CivicPoll> = _polls.value.find { it.id == pollId }?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
    
    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): Result<CivicPoll> {
        val poll = CivicPoll(id = "web_${System.currentTimeMillis()}", scope = scope, districtId = districtId, localId = localId, authorPubKey = "web_admin", question = question, options = options.mapIndexed { i, s -> PollOption("opt_$i", s, 0, 0f) }, status = PollStatus.ACTIVE, createdAt = System.currentTimeMillis(), closesAt = closesAt ?: (System.currentTimeMillis() + 86400000), totalVotes = 0)
        _polls.update { it + poll }
        return Result.success(poll)
    }

    override suspend fun vote(pollId: String, optionId: String, voterPubKey: String): Result<Unit> = Result.success(Unit)
    override suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String): Result<Unit> = Result.success(Unit)
    override fun observePollsPaged(districtId: String, limit: Int, offset: Int): Flow<List<CivicPoll>> = observeDistrictPolls(districtId)
    override fun observePollPosts(pollId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeThreadedPosts(parentPostId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override suspend fun createPost(pollId: String, optionId: String, authorName: String, content: String, headline: String?, parentPostId: String?): Result<PollPost> = Result.failure(Exception("Stub"))
    override suspend fun voteOnPost(postId: String, delta: Int): Result<Unit> = Result.success(Unit)
    override suspend fun getPost(postId: String): Result<PollPost> = Result.failure(Exception("Stub"))
    override suspend fun getAllPolls(): List<CivicPoll> = _polls.value
    override suspend fun getPollsForJurisdictions(jurisdictionIds: List<String>, since: Long): List<CivicPoll> = _polls.value.filter { it.districtId in jurisdictionIds }
    override suspend fun syncPoll(poll: CivicPoll) { _polls.update { (it.filter { p -> p.id != poll.id } + poll) } }
}

class WebResidentRepository : ResidentRepository {
    override fun observeProfile(pubKey: String): Flow<ResidentProfile?> = flowOf(null)
    override fun observeProfileByFingerprint(fingerprint: String): Flow<ResidentProfile?> = flowOf(null)
    override suspend fun getResidentCountAtAddress(fingerprint: String): Int = 0
    override suspend fun getVouchCount(notaryPubKey: String): Int = 0
    override suspend fun getProfile(pubKey: String): Result<ResidentProfile> = Result.failure(Exception("Not found"))
    override suspend fun upgradeTier(pubKey: String, newTier: VerificationTier, proofToken: String): Result<ResidentProfile> = Result.failure(Exception("Stub"))
    override suspend fun upgradeTierWithFingerprint(pubKey: String, newTier: VerificationTier, proofToken: String, fingerprint: String): Result<ResidentProfile> = Result.failure(Exception("Stub"))
    override suspend fun upgradeTierFull(pubKey: String, newTier: VerificationTier, fingerprint: String, verifiedBy: String?): Result<Unit> = Result.success(Unit)
    override suspend fun updateProfile(pubKey: String, displayName: String, avatarUrl: String?): Result<ResidentProfile> = Result.failure(Exception("Stub"))
    override suspend fun updateDistrict(pubKey: String, districtId: String): Result<Unit> = Result.success(Unit)
    override fun observeProfilesVerifiedBy(verifierPubKey: String): Flow<List<ResidentProfile>> = flowOf(emptyList())
    override suspend fun createProfile(profile: ResidentProfile) {}
}

class WebSessionStorage : SessionStorage {
    override fun saveSession(session: UserSession) {
        localStorageSet("session_pubKey", session.pubKey)
        localStorageSet("session_displayName", session.displayName)
        localStorageSet("session_districtId", session.districtId ?: "")
        localStorageSet("session_tier", session.tier.name)
    }
    override fun getSession(): UserSession? {
        val pk = localStorageGet("session_pubKey") ?: return null
        val dn = localStorageGet("session_displayName") ?: ""
        val di = localStorageGet("session_districtId")?.ifBlank { null }
        val t = localStorageGet("session_tier") ?: "OBSERVER"
        return UserSession(pk, dn, di, tier = VerificationTier.valueOf(t))
    }
    override fun clearSession() {
        localStorageRemove("session_pubKey")
        localStorageRemove("session_displayName")
        localStorageRemove("session_districtId")
        localStorageRemove("session_tier")
    }
    override fun savePrivateKeySecurely(key: String) { localStorageSet("session_privateKey", key) }
    override fun getPrivateKeySecurely(): String? = localStorageGet("session_privateKey")
}

class WebAccountRepository : AccountRepository {
    override suspend fun register(account: UserAccount): Result<Unit> = Result.success(Unit)
    override suspend fun login(username: String, password: String): Result<UserAccount> {
         // Mock login for demo
         return Result.success(UserAccount(username, password, "web_pub_${username}", "web_priv", "us-wa-07"))
    }
    override suspend fun changePassword(username: String, newPassword: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateDistrict(username: String, districtId: String) {}
}

class WebVerificationRequestRepository : VerificationRequestRepository {
    override fun observeRequestsForDistrict(districtId: String): Flow<List<VerificationRequest>> = flowOf(emptyList())
    override fun observeRequestsForState(stateId: String): Flow<List<VerificationRequest>> = flowOf(emptyList())
    override suspend fun createRequest(request: VerificationRequest): Result<Unit> = Result.success(Unit)
    override suspend fun updateRequestStatus(requestId: String, status: VerificationRequestStatus, handledBy: String): Result<Unit> = Result.success(Unit)
    override suspend fun getRequest(requestId: String): Result<VerificationRequest> = Result.failure(Exception("Not found"))
}

class WebManifestoRepository : ManifestoRepository {
    override fun observeManifestos(districtId: String): Flow<List<CandidateManifesto>> = flowOf(emptyList())
    override suspend fun getManifesto(manifestoId: String): Result<CandidateManifesto> = Result.failure(Exception("Not found"))
    override suspend fun publishManifesto(districtId: String, title: String, body: String, candidatePubKey: String): Result<CandidateManifesto> = Result.failure(Exception("Stub"))
    override suspend fun askQuestion(manifestoId: String, questionText: String, askerPubKey: String): Result<ManifestoQuestion> = Result.failure(Exception("Stub"))
    override suspend fun answerQuestion(manifestoId: String, questionId: String, answerText: String, candidatePubKey: String): Result<ManifestoQuestion> = Result.failure(Exception("Stub"))
}

class WebScorecardRepository : ScorecardRepository {
    override fun observeScorecard(districtId: String): Flow<RepresentativeScorecard?> = flowOf(null)
    override suspend fun getScorecard(districtId: String): Result<RepresentativeScorecard> = Result.failure(Exception("Not found"))
    override suspend fun submitMetricReport(districtId: String, category: String, name: String, value: String, unit: String, reporterPubKey: String): Result<DistrictMetric> = Result.failure(Exception("Stub"))
}

class WebCommunityRepository : CommunityRepository {
    override fun observePosts(districtId: String, kind: CommunityPostKind?): Flow<List<CommunityPost>> = flowOf(emptyList())
    override suspend fun getPost(postId: String): Result<CommunityPost> = Result.failure(Exception("Not found"))
    override suspend fun createPost(districtId: String, authorPubKey: String, kind: CommunityPostKind, title: String, description: String, price: Double?, location: String?, contactInfo: String?): Result<CommunityPost> = Result.failure(Exception("Stub"))
    override suspend fun deletePost(postId: String): Result<Unit> = Result.success(Unit)
    override suspend fun getAllPosts(): List<CommunityPost> = emptyList()
    override suspend fun syncPost(post: CommunityPost) {}
}

class WebDistrictRepository : DistrictRepository {
    override fun observeDistrict(districtId: String): Flow<District?> = flowOf(null)
    override suspend fun getDistrict(districtId: String): Result<District> = Result.failure(Exception("Not found"))
    override fun observeMetrics(districtId: String): Flow<List<DistrictMetric>> = flowOf(emptyList())
    override suspend fun refreshMetrics(districtId: String): Result<List<DistrictMetric>> = Result.success(emptyList())
}

class WebWtgBackendApi(httpClient: HttpClient) : WtgBackendApi(httpClient = httpClient)
class WebCivicApi(httpClient: HttpClient) : CivicApi {
    override suspend fun fetchPolls(districtId: String, limit: Int, before: Long?): List<CivicPoll> = emptyList()
    override suspend fun fetchPoll(pollId: String): CivicPoll = throw Exception("Stub")
    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): CivicPoll = throw Exception("Stub")
    override suspend fun getRepresentativeVote(legislationId: String): String? = null
    override suspend fun fetchScorecard(districtId: String): RepresentativeScorecard = throw Exception("Stub")
    override suspend fun fetchMetrics(districtId: String): List<DistrictMetric> = emptyList()
    override suspend fun fetchManifestos(districtId: String): List<CandidateManifesto> = emptyList()
    override suspend fun fetchManifesto(manifestoId: String): CandidateManifesto = throw Exception("Stub")
    override suspend fun fetchProfile(pubKey: String): ResidentProfile = throw Exception("Stub")
    override suspend fun upgradeTier(pubKey: String, proofToken: String, targetTier: VerificationTier): ResidentProfile = throw Exception("Stub")
    override suspend fun fetchDistrict(districtId: String): District = District(districtId, DistrictLevel.FEDERAL_HOUSE, "WA", 7, "WA 7", "WA 7")
    override suspend fun detectDistrict(latitude: Double, longitude: Double): District = fetchDistrict("us-wa-07")
    override suspend fun refreshDistrictRegistry() {}
    override suspend fun getDistrictFromAddress(address: String): District? = null
    override suspend fun verifyVoterRolls(firstName: String, lastName: String, address: String, districtId: String): Boolean = true
    override suspend fun resolveAddress(address: String): AddressResolution = AddressResolution(address)
}
