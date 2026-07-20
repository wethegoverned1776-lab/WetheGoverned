package net.wetheGoverned.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import net.wetheGoverned.model.*
import net.wetheGoverned.session.*
import net.wetheGoverned.remote.api.*
import net.wetheGoverned.core.*
import io.ktor.client.*
import kotlinx.datetime.Clock
import kotlinx.browser.window
import org.w3c.dom.Storage

private val storage: Storage get() = window.localStorage

private fun localStorageGet(key: String): String? = storage.getItem(key)
private fun localStorageSet(key: String, value: String) = storage.setItem(key, value)
private fun localStorageRemove(key: String) = storage.removeItem(key)

private fun localStorageKeys(): List<String> {
    val keys = mutableListOf<String>()
    for (i in 0 until storage.length) {
        storage.key(i)?.let { keys.add(it) }
    }
    return keys
}

abstract class WebRepository(val typeName: String) {
    protected val json = Json { ignoreUnknownKeys = true }

    protected fun <T> saveToStorage(id: String, data: T, serializer: kotlinx.serialization.KSerializer<T>) {
        localStorageSet("${typeName}_$id", json.encodeToString(serializer, data))
    }

    protected fun <T> loadFromStorage(id: String, serializer: kotlinx.serialization.KSerializer<T>): T? {
        return localStorageGet("${typeName}_$id")?.let { json.decodeFromString(serializer, it) }
    }
    
    protected fun listIdsFromStorage(): List<String> {
        val prefix = "${typeName}_"
        return localStorageKeys().filter { it.startsWith(prefix) }.map { it.removePrefix(prefix) }
    }
}

class WebVoteRepository(private val publisher: CivicPublisher? = null) : VoteRepository, WebRepository("votes") {
    override fun observeAllVotes(): Flow<List<CivicVote>> = flow {
        emit(listIdsFromStorage().mapNotNull { id -> loadFromStorage(id, CivicVote.serializer()) })
    }
    override fun observeVotesByUser(pubKey: String): Flow<List<CivicVote>> = flow {
        emit(listIdsFromStorage().mapNotNull { id -> loadFromStorage(id, CivicVote.serializer()) }.filter { it.voterPubKey == pubKey })
    }
    override suspend fun flagVote(voteId: String, reason: String, expiresAt: Long): Result<Unit> = Result.success(Unit)
    override suspend fun disputeVote(voteId: String, comment: String): Result<Unit> = Result.success(Unit)
    override suspend fun resolveVote(voteId: String): Result<Unit> = Result.success(Unit)
    override suspend fun syncVote(vote: CivicVote) { saveToStorage(vote.id, vote, CivicVote.serializer()) }
}

class WebPollRepository(private val publisher: CivicPublisher? = null) : PollRepository, WebRepository("polls") {
    private val _pollsFlow = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    override fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>> = _pollsFlow.onStart { emit(Unit) }.map {
        listIdsFromStorage().mapNotNull { id -> loadFromStorage(id, CivicPoll.serializer()) }
            .filter { it.districtId == districtId || it.districtId == "us" }
    }

    override fun observePollsByIds(districtIds: List<String>): Flow<List<CivicPoll>> = _pollsFlow.onStart { emit(Unit) }.map {
        listIdsFromStorage().mapNotNull { id -> loadFromStorage(id, CivicPoll.serializer()) }
            .filter { it.districtId in districtIds }
    }

    override fun observePollsByScope(scope: PollScope, districtId: String): Flow<List<CivicPoll>> = _pollsFlow.onStart { emit(Unit) }.map {
        listIdsFromStorage().mapNotNull { id -> loadFromStorage(id, CivicPoll.serializer()) }
            .filter { it.scope == scope && (it.districtId == districtId || it.districtId == "us") }
    }

    override suspend fun getPoll(pollId: String): Result<CivicPoll> = 
        loadFromStorage(pollId, CivicPoll.serializer())?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))

    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): Result<CivicPoll> {
        val id = "poll_${Clock.System.now().toEpochMilliseconds()}"
        val newPoll = CivicPoll(
            id = id, scope = scope, districtId = districtId, localId = localId,
            authorPubKey = "web_user", question = question,
            options = options.mapIndexed { i, s -> PollOption("opt_$i", s, 0, 0f) },
            status = PollStatus.ACTIVE, createdAt = Clock.System.now().toEpochMilliseconds(),
            closesAt = closesAt ?: (Clock.System.now().toEpochMilliseconds() + 86400000), totalVotes = 0
        )
        saveToStorage(id, newPoll, CivicPoll.serializer())
        
        publisher?.signPublishImportCivicEvent(
            kind = when(scope) {
                PollScope.FEDERAL -> CivicEventKind.FEDERAL_POLL
                PollScope.STATE -> CivicEventKind.STATE_POLL
                else -> CivicEventKind.DISTRICT_POLL
            },
            tags = listOf("d", districtId),
            content = json.encodeToString(CivicPoll.serializer(), newPoll),
            pubKey = "web_user"
        )
        
        _pollsFlow.emit(Unit)
        return Result.success(newPoll)
    }

    override suspend fun vote(pollId: String, optionId: String, voterPubKey: String): Result<Unit> {
        val poll = loadFromStorage(pollId, CivicPoll.serializer()) ?: return Result.failure(Exception("Poll not found"))
        val updatedOptions = poll.options.map { opt -> if (opt.id == optionId) opt.copy(voteCount = opt.voteCount + 1) else opt }
        val newTotal = poll.totalVotes + 1
        val updatedPoll = poll.copy(options = updatedOptions.map { it.copy(percentageOfTotal = it.voteCount.toFloat() / newTotal) }, totalVotes = newTotal, residentVoteOption = optionId)
        saveToStorage(pollId, updatedPoll, CivicPoll.serializer())

        val vote = CivicVote(
            id = "vote_${pollId}_$voterPubKey",
            pollId = pollId,
            optionId = optionId,
            voterPubKey = voterPubKey,
            voterName = "Web Resident",
            timestamp = Clock.System.now().toEpochMilliseconds(),
            nonce = 0L,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        publisher?.signPublishImportCivicEvent(
            kind = CivicEventKind.POLL_VOTE,
            tags = listOf("d", pollId),
            content = json.encodeToString(CivicVote.serializer(), vote),
            pubKey = voterPubKey
        )

        _pollsFlow.emit(Unit)
        return Result.success(Unit)
    }

    override suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String): Result<Unit> {
        val poll = loadFromStorage(pollId, CivicPoll.serializer()) ?: return Result.failure(Exception("Poll not found"))
        saveToStorage(pollId, poll.copy(importanceScore = poll.importanceScore + delta), CivicPoll.serializer())
        _pollsFlow.emit(Unit)
        return Result.success(Unit)
    }

    override fun observePollsPaged(districtId: String, limit: Int, offset: Int): Flow<List<CivicPoll>> = _pollsFlow.onStart { emit(Unit) }.map {
        listIdsFromStorage().mapNotNull { id -> loadFromStorage(id, CivicPoll.serializer()) }
            .filter { it.districtId == districtId }.drop(offset).take(limit)
    }

    override fun observePollPosts(pollId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeThreadedPosts(parentPostId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override suspend fun createPost(pollId: String, optionId: String, authorName: String, content: String, headline: String?, parentPostId: String?): Result<PollPost> = Result.failure(Exception("Not implemented"))
    override suspend fun voteOnPost(postId: String, delta: Int): Result<Unit> = Result.success(Unit)
    override suspend fun getPost(postId: String): Result<PollPost> = Result.failure(Exception("Not found"))
    override suspend fun getAllPolls(): List<CivicPoll> = listIdsFromStorage().mapNotNull { id -> loadFromStorage(id, CivicPoll.serializer()) }
    override suspend fun getPollsForJurisdictions(jurisdictionIds: List<String>, since: Long): List<CivicPoll> = getAllPolls().filter { (it.districtId in jurisdictionIds || it.localId in jurisdictionIds) && it.createdAt > since }
    
    override suspend fun syncPoll(poll: CivicPoll) {
        saveToStorage(poll.id, poll, CivicPoll.serializer())
        _pollsFlow.emit(Unit)
    }
}

class WebResidentRepository : ResidentRepository, WebRepository("residents") {
    private val _updates = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    override fun observeProfile(pubKey: String): Flow<ResidentProfile?> = _updates.onStart { emit(Unit) }.map {
        loadFromStorage(pubKey, ResidentProfile.serializer())
    }

    override fun observeProfileByFingerprint(fingerprint: String): Flow<ResidentProfile?> = _updates.onStart { emit(Unit) }.map {
        listIdsFromStorage().mapNotNull { loadFromStorage(it, ResidentProfile.serializer()) }.find { it.addressFingerprint == fingerprint }
    }

    override suspend fun getResidentCountAtAddress(fingerprint: String): Int = 0
    override suspend fun getVouchCount(notaryPubKey: String): Int = 0
    override suspend fun getProfile(pubKey: String): Result<ResidentProfile> = 
        loadFromStorage(pubKey, ResidentProfile.serializer())?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
        
    override suspend fun upgradeTier(pubKey: String, newTier: VerificationTier, proofToken: String): Result<ResidentProfile> = Result.failure(Exception("Not implemented"))
    override suspend fun upgradeTierWithFingerprint(pubKey: String, newTier: VerificationTier, proofToken: String, fingerprint: String): Result<ResidentProfile> = Result.failure(Exception("Not implemented"))
    override suspend fun upgradeTierFull(pubKey: String, newTier: VerificationTier, fingerprint: String, verifiedBy: String?): Result<Unit> = Result.success(Unit)
    override suspend fun updateProfile(pubKey: String, displayName: String, avatarUrl: String?): Result<ResidentProfile> = Result.failure(Exception("Not implemented"))
    override suspend fun updateDistrict(pubKey: String, districtId: String): Result<Unit> {
        val p = loadFromStorage(pubKey, ResidentProfile.serializer()) ?: return Result.failure(Exception("Not found"))
        saveToStorage(pubKey, p.copy(federalHouseId = districtId), ResidentProfile.serializer())
        _updates.emit(Unit)
        return Result.success(Unit)
    }
    override fun observeProfilesVerifiedBy(verifierPubKey: String): Flow<List<ResidentProfile>> = flowOf(emptyList())
    override suspend fun createProfile(profile: ResidentProfile) {
        saveToStorage(profile.pubKey, profile, ResidentProfile.serializer())
        _updates.emit(Unit)
    }
}

class WebManifestoRepository : ManifestoRepository {
    override fun observeManifestos(districtId: String): Flow<List<CandidateManifesto>> = flowOf(emptyList())
    override suspend fun getManifesto(manifestoId: String): Result<CandidateManifesto> = Result.failure(Exception("Not found"))
    override suspend fun publishManifesto(districtId: String, title: String, body: String, candidatePubKey: String): Result<CandidateManifesto> = Result.failure(Exception("Not implemented"))
    override suspend fun askQuestion(manifestoId: String, questionText: String, askerPubKey: String): Result<ManifestoQuestion> = Result.failure(Exception("Not implemented"))
    override suspend fun answerQuestion(manifestoId: String, questionId: String, answerText: String, candidatePubKey: String): Result<ManifestoQuestion> = Result.failure(Exception("Not implemented"))
}

class WebScorecardRepository : ScorecardRepository {
    override fun observeScorecard(districtId: String): Flow<RepresentativeScorecard?> = flowOf(null)
    override suspend fun getScorecard(districtId: String): Result<RepresentativeScorecard> = Result.failure(Exception("Not found"))
    override suspend fun submitMetricReport(districtId: String, category: String, name: String, value: String, unit: String, reporterPubKey: String): Result<DistrictMetric> = Result.failure(Exception("Not implemented"))
}

class WebDistrictRepository : DistrictRepository {
    override fun observeDistrict(districtId: String): Flow<District?> = flowOf(null)
    override suspend fun getDistrict(districtId: String): Result<District> = Result.failure(Exception("Not found"))
    override fun observeMetrics(districtId: String): Flow<List<DistrictMetric>> = flowOf(emptyList())
    override suspend fun refreshMetrics(districtId: String): Result<List<DistrictMetric>> = Result.success(emptyList())
}

class WebCommunityRepository(private val publisher: CivicPublisher? = null) : CommunityRepository, WebRepository("community") {
    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    override fun observePosts(districtId: String, kind: CommunityPostKind?): Flow<List<CommunityPost>> = _posts.map { list -> list.filter { it.districtId == districtId && (kind == null || it.kind == kind) } }
    override suspend fun getPost(postId: String): Result<CommunityPost> = _posts.value.find { it.id == postId }?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
    override suspend fun createPost(districtId: String, authorPubKey: String, kind: CommunityPostKind, title: String, description: String, price: Double?, location: String?, contactInfo: String?): Result<CommunityPost> {
        val post = CommunityPost(
            id = "post_${Clock.System.now().toEpochMilliseconds()}",
            districtId = districtId,
            authorPubKey = authorPubKey,
            kind = kind,
            title = title,
            description = description,
            price = price,
            location = location,
            contactInfo = contactInfo,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        _posts.update { it + post }
        publisher?.signPublishImportCivicEvent(
            kind = CivicEventKind.COMMUNITY_POST,
            tags = listOf("d", districtId),
            content = json.encodeToString(CommunityPost.serializer(), post),
            pubKey = authorPubKey
        )
        return Result.success(post)
    }
    override suspend fun deletePost(postId: String): Result<Unit> = runCatching { _posts.update { it.filter { p -> p.id != postId } } }
    override suspend fun getAllPosts(): List<CommunityPost> = _posts.value
    override suspend fun syncPost(post: CommunityPost) { _posts.update { if (it.none { p -> p.id == post.id }) it + post else it } }
}

class WebVerificationRequestRepository : VerificationRequestRepository {
    override fun observeRequestsForDistrict(districtId: String): Flow<List<VerificationRequest>> = flowOf(emptyList())
    override fun observeRequestsForState(stateId: String): Flow<List<VerificationRequest>> = flowOf(emptyList())
    override suspend fun createRequest(request: VerificationRequest): Result<Unit> = Result.success(Unit)
    override suspend fun updateRequestStatus(requestId: String, status: VerificationRequestStatus, handledBy: String): Result<Unit> = Result.success(Unit)
    override suspend fun getRequest(requestId: String): Result<VerificationRequest> = Result.failure(Exception("Not found"))
}

class WebAccountRepository : AccountRepository {
    override suspend fun register(account: UserAccount): Result<Unit> = Result.success(Unit)
    override suspend fun login(username: String, password: String): Result<UserAccount> {
        if (username == "admin" && password == "1January012@") return Result.success(UserAccount("admin", "1January012@", "pub_admin", "priv_admin", "us-fl-06"))
        return Result.success(UserAccount(username, password, "pub_$username", "priv_$username", "us-fl-06"))
    }
    override suspend fun changePassword(username: String, newPassword: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateDistrict(username: String, districtId: String) {}
}

class WebSessionStorage : SessionStorage {
    override fun saveSession(session: UserSession) {
        localStorageSet("pubKey", session.pubKey)
        localStorageSet("displayName", session.displayName)
        session.districtId?.let { localStorageSet("districtId", it) }
        localStorageSet("tier", session.tier.name)
        session.privateKey?.let { localStorageSet("privateKey", it) }
    }
    override fun getSession(): UserSession? {
        val pk = localStorageGet("pubKey") ?: return null
        return UserSession(
            pk,
            localStorageGet("displayName") ?: "",
            localStorageGet("districtId"),
            tier = VerificationTier.valueOf(localStorageGet("tier") ?: "OBSERVER"),
            privateKey = localStorageGet("privateKey")
        )
    }
    override fun clearSession() {
        localStorageRemove("pubKey")
        localStorageRemove("displayName")
        localStorageRemove("districtId")
        localStorageRemove("tier")
        localStorageRemove("privateKey")
    }
    override fun savePrivateKeySecurely(key: String) { localStorageSet("privateKey", key) }
    override fun getPrivateKeySecurely(): String? = localStorageGet("privateKey")
}

class WebWtgBackendApi(private val httpClient: HttpClient) : WtgBackendApi(baseUrl = "https://sim.wetheGoverned.net", httpClient = httpClient) {
    override suspend fun sendVerificationEmail(pubKey: String, email: String, districtId: String): SendEmailResponse = SendEmailResponse(true, "Simulated")
    override suspend fun confirmEmailToken(pubKey: String, token: String): ProofTokenResponse = ProofTokenResponse(proofToken = "sim_proof_${pubKey.take(8)}", tier = 1)
}

class WebCivicApi(private val httpClient: HttpClient) : CivicApi {
    override suspend fun fetchPolls(districtId: String, limit: Int, before: Long?): List<CivicPoll> = emptyList()
    override suspend fun fetchPoll(pollId: String): CivicPoll = throw Exception("Not implemented")
    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): CivicPoll = throw Exception("Not implemented")
    override suspend fun getRepresentativeVote(legislationId: String): String? = null
    override suspend fun fetchScorecard(districtId: String): RepresentativeScorecard = throw Exception("Not implemented")
    override suspend fun fetchMetrics(districtId: String): List<DistrictMetric> = emptyList()
    override suspend fun fetchManifestos(districtId: String): List<CandidateManifesto> = emptyList()
    override suspend fun fetchManifesto(manifestoId: String): CandidateManifesto = throw Exception("Not implemented")
    override suspend fun fetchProfile(pubKey: String): ResidentProfile = throw Exception("Not implemented")
    override suspend fun upgradeTier(pubKey: String, proofToken: String, targetTier: VerificationTier): ResidentProfile = throw Exception("Not implemented")
    override suspend fun fetchDistrict(districtId: String): District = District(
        id = districtId, level = DistrictLevel.FEDERAL_HOUSE, state = "US", districtNumber = 0, name = "District $districtId", displayName = "District $districtId"
    )
    override suspend fun detectDistrict(latitude: Double, longitude: Double): District = fetchDistrict("us-wa-07")
    override suspend fun refreshDistrictRegistry() {}
    override suspend fun getDistrictFromAddress(address: String): District? = resolveAddress(address).federalDistrict
    override suspend fun verifyVoterRolls(firstName: String, lastName: String, address: String, districtId: String): Boolean = true
    override suspend fun resolveAddress(address: String): AddressResolution = AddressResolution(
        address = address,
        federalDistrict = District(id = "us-fl-06", level = DistrictLevel.FEDERAL_HOUSE, state = "FL", districtNumber = 6, name = "Florida's 6th District", displayName = "FL-06"),
        stateUpperDistrict = District(id = "us-fl-senate-07", level = DistrictLevel.STATE_SENATE, state = "FL", districtNumber = 7, name = "Florida Senate District 7", displayName = "FL Senate-07"),
        stateLowerDistrict = District(id = "us-fl-house-19", level = DistrictLevel.STATE_HOUSE, state = "FL", districtNumber = 19, name = "Florida House District 19", displayName = "FL House-19"),
        localJurisdiction = "Palm Coast, Flagler County"
    )
}

class LocationHelper {
    suspend fun getCurrentLocation(): Pair<Double, Double> = 29.3751 to -81.2995
}
