package net.wetheGoverned.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import net.wetheGoverned.model.*
import net.wetheGoverned.session.*
import net.wetheGoverned.remote.api.*
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

    protected fun saveToStorage(id: String, content: String) {
        localStorageSet("${typeName}_$id", content)
    }

    protected fun loadFromStorage(id: String): String? {
        return localStorageGet("${typeName}_$id")
    }
    
    protected fun listIdsFromStorage(): List<String> {
        val prefix = "${typeName}_"
        return localStorageKeys().filter { it.startsWith(prefix) }.map { it.removePrefix(prefix) }
    }
}

class WebVoteRepository : VoteRepository, WebRepository("votes") {
    override fun observeAllVotes(): Flow<List<CivicVote>> = flow {
        emit(listIdsFromStorage().mapNotNull { id ->
            loadFromStorage(id)?.let { json.decodeFromString(CivicVote.serializer(), it) }
        })
    }
    override fun observeVotesByUser(pubKey: String): Flow<List<CivicVote>> = observeAllVotes().map { list ->
        list.filter { it.voterPubKey == pubKey }
    }
    override suspend fun flagVote(voteId: String, reason: String, expiresAt: Long): Result<Unit> = Result.success(Unit)
    override suspend fun disputeVote(voteId: String, comment: String): Result<Unit> = Result.success(Unit)
    override suspend fun resolveVote(voteId: String): Result<Unit> = Result.success(Unit)
    override suspend fun syncVote(vote: CivicVote) { saveToStorage(vote.id, json.encodeToString(CivicVote.serializer(), vote)) }
}

class WebPollRepository : PollRepository, WebRepository("polls") {
    private val _polls = MutableStateFlow<List<CivicPoll>>(emptyList())
    
    init {
        val storedIds = listIdsFromStorage()
        if (storedIds.isEmpty()) {
            val now = Clock.System.now().toEpochMilliseconds()
            val adminPoll = CivicPoll(
                id = "poll_fed_1", 
                scope = PollScope.FEDERAL, 
                districtId = "us", 
                authorPubKey = "admin", 
                question = "Should the US implement term limits for Congress?", 
                options = listOf(PollOption("opt_1", "Yes, 12 years", 5000, 0.9f)), 
                status = PollStatus.ACTIVE, 
                createdAt = now, 
                closesAt = now + 86400000 * 30, 
                totalVotes = 5500
            )
            saveToStorage(adminPoll.id, json.encodeToString(CivicPoll.serializer(), adminPoll))
            _polls.value = listOf(adminPoll)
        } else {
            _polls.value = storedIds.mapNotNull { id ->
                loadFromStorage(id)?.let { json.decodeFromString(CivicPoll.serializer(), it) }
            }
        }
    }

    override fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>> = _polls.map { list -> 
        list.filter { it.districtId == districtId || it.districtId == "us" } 
    }
    override fun observePollsByIds(districtIds: List<String>): Flow<List<CivicPoll>> = _polls.map { list -> 
        list.filter { it.districtId in districtIds } 
    }
    override fun observePollsByScope(scope: PollScope, districtId: String): Flow<List<CivicPoll>> = _polls.map { list -> 
        list.filter { it.scope == scope } 
    }
    override suspend fun getPoll(pollId: String): Result<CivicPoll> = 
        _polls.value.find { it.id == pollId }?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
    
    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): Result<CivicPoll> {
        val now = Clock.System.now().toEpochMilliseconds()
        val poll = CivicPoll(
            id = "poll_$now", 
            scope = scope, 
            districtId = districtId, 
            localId = localId, 
            authorPubKey = "admin", 
            question = question, 
            options = options.mapIndexed { i, s -> PollOption("opt_$i", s, 0, 0f) }, 
            status = PollStatus.ACTIVE, 
            createdAt = now, 
            closesAt = closesAt ?: (now + 86400000), 
            totalVotes = 0
        )
        saveToStorage(poll.id, json.encodeToString(CivicPoll.serializer(), poll))
        _polls.update { it + poll }
        return Result.success(poll)
    }

    override suspend fun vote(pollId: String, optionId: String, voterPubKey: String): Result<Unit> {
        val poll = _polls.value.find { it.id == pollId } ?: return Result.failure(Exception("Poll not found"))
        val updatedOptions = poll.options.map { opt -> if (opt.id == optionId) opt.copy(voteCount = opt.voteCount + 1) else opt }
        val newTotal = poll.totalVotes + 1
        val updatedPoll = poll.copy(options = updatedOptions.map { it.copy(percentageOfTotal = it.voteCount.toFloat() / newTotal) }, totalVotes = newTotal, residentVoteOption = optionId)
        saveToStorage(pollId, json.encodeToString(CivicPoll.serializer(), updatedPoll))
        _polls.update { list -> list.map { if (it.id == pollId) updatedPoll else it } }
        return Result.success(Unit)
    }

    override suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String): Result<Unit> {
        val poll = _polls.value.find { it.id == pollId } ?: return Result.failure(Exception("Poll not found"))
        val updatedPoll = poll.copy(importanceScore = poll.importanceScore + delta)
        saveToStorage(pollId, json.encodeToString(CivicPoll.serializer(), updatedPoll))
        _polls.update { list -> list.map { if (it.id == pollId) updatedPoll else it } }
        return Result.success(Unit)
    }

    override fun observePollsPaged(districtId: String, limit: Int, offset: Int): Flow<List<CivicPoll>> = observeDistrictPolls(districtId)
    override fun observePollPosts(pollId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeThreadedPosts(parentPostId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override suspend fun createPost(pollId: String, optionId: String, authorName: String, content: String, headline: String?, parentPostId: String?): Result<PollPost> = Result.failure(Exception("Stub"))
    override suspend fun voteOnPost(postId: String, delta: Int): Result<Unit> = Result.success(Unit)
    override suspend fun getPost(postId: String): Result<PollPost> = Result.failure(Exception("Stub"))
    override suspend fun getAllPolls(): List<CivicPoll> = _polls.value
    override suspend fun getPollsForJurisdictions(jurisdictionIds: List<String>, since: Long): List<CivicPoll> = _polls.value.filter { it.districtId in jurisdictionIds }
    override suspend fun syncPoll(poll: CivicPoll) { 
        saveToStorage(poll.id, json.encodeToString(CivicPoll.serializer(), poll))
        _polls.update { (it.filter { p -> p.id != poll.id } + poll) } 
    }
}

class WebResidentRepository : ResidentRepository, WebRepository("residents") {
    private val _updates = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    init {
        if (loadFromStorage("pub_admin") == null) {
            val admin = ResidentProfile(
                pubKey = "pub_admin", 
                displayName = "Admin", 
                districtId = "us-fl-06", 
                tier = VerificationTier.VERIFIED, 
                joinedAt = Clock.System.now().toEpochMilliseconds(), 
                address = "172 beech wood lane palm coast fl 32137", 
                isVerified = true
            )
            saveToStorage(admin.pubKey, json.encodeToString(ResidentProfile.serializer(), admin))
        }
    }

    override fun observeProfile(pubKey: String): Flow<ResidentProfile?> = _updates.flatMapLatest {
        flow { emit(loadFromStorage(pubKey)?.let { json.decodeFromString(ResidentProfile.serializer(), it) }) }
    }
    
    override fun observeProfileByFingerprint(fingerprint: String): Flow<ResidentProfile?> = _updates.flatMapLatest {
        flow {
            emit(listIdsFromStorage().mapNotNull { id ->
                loadFromStorage(id)?.let { json.decodeFromString(ResidentProfile.serializer(), it) }
            }.find { it.addressFingerprint == fingerprint })
        }
    }

    override suspend fun getResidentCountAtAddress(fingerprint: String): Int = 0
    override suspend fun getVouchCount(notaryPubKey: String): Int = 0
    override suspend fun getProfile(pubKey: String): Result<ResidentProfile> = 
        loadFromStorage(pubKey)?.let { Result.success(json.decodeFromString(ResidentProfile.serializer(), it)) } 
        ?: Result.failure(Exception("Not found"))

    override suspend fun upgradeTier(pubKey: String, newTier: VerificationTier, proofToken: String): Result<ResidentProfile> = Result.failure(Exception("Stub"))
    override suspend fun upgradeTierWithFingerprint(pubKey: String, newTier: VerificationTier, proofToken: String, fingerprint: String): Result<ResidentProfile> = Result.failure(Exception("Stub"))
    override suspend fun upgradeTierFull(pubKey: String, newTier: VerificationTier, fingerprint: String, verifiedBy: String?): Result<Unit> = Result.success(Unit)
    override suspend fun updateProfile(pubKey: String, displayName: String, avatarUrl: String?): Result<ResidentProfile> {
        val p = (getProfile(pubKey).getOrNull() ?: return Result.failure(Exception("Not found"))).copy(displayName = displayName, avatarUrl = avatarUrl)
        saveToStorage(pubKey, json.encodeToString(ResidentProfile.serializer(), p))
        _updates.emit(Unit)
        return Result.success(p)
    }
    override suspend fun updateDistrict(pubKey: String, districtId: String): Result<Unit> {
        val p = (getProfile(pubKey).getOrNull() ?: return Result.failure(Exception("Not found"))).copy(districtId = districtId)
        saveToStorage(pubKey, json.encodeToString(ResidentProfile.serializer(), p))
        _updates.emit(Unit)
        return Result.success(Unit)
    }
    override fun observeProfilesVerifiedBy(verifierPubKey: String): Flow<List<ResidentProfile>> = flowOf(emptyList())
    override suspend fun createProfile(profile: ResidentProfile) {
        saveToStorage(profile.pubKey, json.encodeToString(ResidentProfile.serializer(), profile))
        _updates.emit(Unit)
    }
}

class WebSessionStorage : SessionStorage {
    override fun saveSession(session: UserSession) {
        localStorageSet("session_pubKey", session.pubKey)
        localStorageSet("session_displayName", session.displayName)
        localStorageSet("session_districtId", session.districtId ?: "")
        localStorageSet("session_tier", session.tier.name)
        localStorageSet("session_privateKey", session.privateKey ?: "")
    }
    override fun getSession(): UserSession? {
        val pk = localStorageGet("session_pubKey") ?: return null
        val dn = localStorageGet("session_displayName") ?: ""
        val di = localStorageGet("session_districtId")?.ifBlank { null }
        val t = localStorageGet("session_tier") ?: "OBSERVER"
        val sk = localStorageGet("session_privateKey") ?: ""
        val tier = try { VerificationTier.valueOf(t) } catch (e: Exception) { VerificationTier.OBSERVER }
        return UserSession(pk, dn, di, tier = tier, privateKey = sk)
    }
    override fun clearSession() {
        localStorageRemove("session_pubKey")
        localStorageRemove("session_displayName")
        localStorageRemove("session_districtId")
        localStorageRemove("session_tier")
        localStorageRemove("session_privateKey")
    }
    override fun savePrivateKeySecurely(key: String) { localStorageSet("session_privateKey", key) }
    override fun getPrivateKeySecurely(): String? = localStorageGet("session_privateKey")
}

class WebAccountRepository : AccountRepository, WebRepository("accounts") {
    override suspend fun register(account: UserAccount): Result<Unit> {
        if (loadFromStorage(account.username) != null) return Result.failure(Exception("Exists"))
        saveToStorage(account.username, json.encodeToString(UserAccount.serializer(), account))
        return Result.success(Unit)
    }

    override suspend fun login(username: String, password: String): Result<UserAccount> {
        if (username == "admin" && password == "1January012@") {
            return Result.success(UserAccount("admin", "1January012@", "pub_admin", "priv_admin", "us-fl-06"))
        }
        val data = loadFromStorage(username) ?: return Result.failure(Exception("Account not found"))
        val acc = json.decodeFromString(UserAccount.serializer(), data)
        return if (acc.password == password) Result.success(acc) else Result.failure(Exception("Invalid password"))
    }

    override suspend fun changePassword(username: String, newPassword: String): Result<Unit> {
        val data = loadFromStorage(username) ?: return Result.failure(Exception("Account not found"))
        val acc = json.decodeFromString(UserAccount.serializer(), data)
        saveToStorage(username, json.encodeToString(UserAccount.serializer(), acc.copy(password = newPassword)))
        return Result.success(Unit)
    }

    override suspend fun updateDistrict(username: String, districtId: String) {
        val data = loadFromStorage(username) ?: return
        val acc = json.decodeFromString(UserAccount.serializer(), data)
        saveToStorage(username, json.encodeToString(UserAccount.serializer(), acc.copy(districtId = districtId)))
    }
}

class WebDistrictRepository : DistrictRepository {
    override fun observeDistrict(districtId: String): Flow<District?> = flowOf(null)
    override suspend fun getDistrict(districtId: String): Result<District> = Result.failure(Exception("Not found"))
    override fun observeMetrics(districtId: String): Flow<List<DistrictMetric>> = flowOf(emptyList())
    override suspend fun refreshMetrics(districtId: String): Result<List<DistrictMetric>> = Result.success(emptyList())
}

class WebVerificationRequestRepository : VerificationRequestRepository, WebRepository("verification_requests") {
    override fun observeRequestsForDistrict(districtId: String): Flow<List<VerificationRequest>> = flow {
        emit(listIdsFromStorage().mapNotNull { id ->
            loadFromStorage(id)?.let { json.decodeFromString(VerificationRequest.serializer(), it) }
        }.filter { it.districtId == districtId })
    }
    override fun observeRequestsForState(stateId: String): Flow<List<VerificationRequest>> = flow {
        emit(listIdsFromStorage().mapNotNull { id ->
            loadFromStorage(id)?.let { json.decodeFromString(VerificationRequest.serializer(), it) }
        }.filter { it.stateId == stateId })
    }
    override suspend fun createRequest(request: VerificationRequest): Result<Unit> {
        saveToStorage(request.id, json.encodeToString(VerificationRequest.serializer(), request))
        return Result.success(Unit)
    }
    override suspend fun updateRequestStatus(requestId: String, status: VerificationRequestStatus, handledBy: String): Result<Unit> = Result.success(Unit)
    override suspend fun getRequest(requestId: String): Result<VerificationRequest> = 
        loadFromStorage(requestId)?.let { Result.success(json.decodeFromString(VerificationRequest.serializer(), it)) } 
        ?: Result.failure(Exception("Not found"))
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
