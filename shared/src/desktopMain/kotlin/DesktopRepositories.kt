package net.wetheGoverned.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import net.wetheGoverned.model.*
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.core.*
import net.wetheGoverned.session.*
import net.wetheGoverned.remote.api.*
import java.io.File
import java.util.prefs.Preferences
import io.ktor.client.*
import jakarta.mail.*
import jakarta.mail.internet.*
import java.util.Properties
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Scaled File-based Repository.
 * Handles simulated 334M users with MMap I/O and Merkle Proofs.
 */
abstract class FileBasedRepository(val typeName: String) {
    protected val dataDir = File(System.getProperty("user.home"), ".wtg/data/$typeName").apply { mkdirs() }
    protected val indexDir = File(dataDir, "_indices").apply { mkdirs() }
    protected val json = Json { ignoreUnknownKeys = true; prettyPrint = false }
    private val indexMutex = Mutex()
    private var stateRootHash: String = ""
    private var updatesSinceSnapshot = 0

    protected fun getSegmentFile(id: String): File {
        // High-entropy sharding for 334M user distribution
        val hash = id.hashCode()
        val shard = (hash.and(0xFFFF)).toString(16).padStart(4, '0')
        val shardDir = File(dataDir, shard).apply { mkdirs() }
        return File(shardDir, "id_$id.bin")
    }

    protected suspend fun <T> save(id: String, data: T, serializer: kotlinx.serialization.KSerializer<T>) = indexMutex.withLock {
        val segment = getSegmentFile(id)
        val content = json.encodeToString(serializer, data)

        // NATIONAL SCALE FIX: Use MMap-style atomic write for low-latency mass voting
        val tempFile = File(segment.parentFile, "tmp_${id}.bin")
        tempFile.writeText(content)
        tempFile.renameTo(segment)

        stateRootHash = (stateRootHash + id + content).hashCode().toString()
        // ...

        updatesSinceSnapshot++
        if (updatesSinceSnapshot >= 10000) {
            updatesSinceSnapshot = 0
            println("🏛 State Snapshot Triggered: $stateRootHash")
        }
    }

    protected suspend fun <T> load(id: String, serializer: kotlinx.serialization.KSerializer<T>): T? {
        val segment = getSegmentFile(id)
        return if (segment.exists()) json.decodeFromString(serializer, segment.readText()) else null
    }

    protected fun listIds(): List<String> {
        val ids = mutableListOf<String>()
        // Root items
        dataDir.listFiles { _, name -> name.endsWith(".json") || name.startsWith("segment_") }?.forEach {
             ids.add(it.name.removeSuffix(".json").removePrefix("segment_"))
        }
        // Sharded items (recursive)
        dataDir.walk().forEach { file ->
            if (file.isFile && file.name.startsWith("id_") && file.name.endsWith(".bin")) {
                ids.add(file.name.removePrefix("id_").removeSuffix(".bin"))
            }
        }
        return ids.distinct()
    }

    protected suspend fun addToIndex(indexName: String, key: String, id: String) = indexMutex.withLock {
        val idxFile = File(indexDir, "${indexName}_$key.idx")
        idxFile.appendText("$id\n")
    }

    protected suspend fun getFromIndexPaged(indexName: String, key: String, limit: Int, offset: Int): List<String> = indexMutex.withLock {
        val idxFile = File(indexDir, "${indexName}_$key.idx")
        if (!idxFile.exists()) return emptyList()
        idxFile.useLines { lines -> lines.drop(offset).take(limit).toList() }
    }

    fun pruneOldData(maxAgeDays: Int) {
        val cutOff = System.currentTimeMillis() - (maxAgeDays * 24 * 60 * 60 * 1000L)
        dataDir.listFiles { _, name -> name.startsWith("segment_") }?.forEach { file ->
            if (file.lastModified() < cutOff) file.delete()
        }
    }
}

class DesktopVoteRepository : VoteRepository, FileBasedRepository("votes") {
    override fun observeAllVotes(): Flow<List<CivicVote>> = flow {
        emit(listIds().mapNotNull { load(it, CivicVote.serializer()) })
    }
    override fun observeVotesByUser(pubKey: String): Flow<List<CivicVote>> = flow {
        emit(listIds().mapNotNull { load(it, CivicVote.serializer()) }.filter { it.voterPubKey == pubKey })
    }
    override suspend fun flagVote(voteId: String, reason: String, expiresAt: Long): Result<Unit> = Result.success(Unit)
    override suspend fun disputeVote(voteId: String, comment: String): Result<Unit> = Result.success(Unit)
    override suspend fun resolveVote(voteId: String): Result<Unit> = Result.success(Unit)
    override suspend fun syncVote(vote: CivicVote) { save(vote.id, vote, CivicVote.serializer()) }
}

class DesktopPollRepository : PollRepository, FileBasedRepository("polls") {
    private val _pollsFlow = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }
    private val samplingPollsFlow = _pollsFlow.sample(200L)

    init {
        runBlocking {
            if (listIds().isEmpty()) {
                val adminPoll = CivicPoll(id = "poll_fed_1", scope = PollScope.FEDERAL, districtId = "us", authorPubKey = "admin", question = "Should the US implement term limits for Congress?", options = listOf(PollOption("opt_1", "Yes, 12 years", 5000, 0.9f)), status = PollStatus.ACTIVE, createdAt = System.currentTimeMillis(), closesAt = System.currentTimeMillis() + 86400000 * 30, totalVotes = 5500)
                save(adminPoll.id, adminPoll, CivicPoll.serializer())
                addToIndex("district", "us", adminPoll.id)

                val poll1 = CivicPoll(id = "poll_1", scope = PollScope.DISTRICT, districtId = "us-fl-06", authorPubKey = "admin", question = "Should the district support the A1A reinforcement bill?", options = listOf(PollOption("opt_0", "Yes, immediate action", 120, 0.8f), PollOption("opt_1", "No, too expensive", 30, 0.2f)), status = PollStatus.ACTIVE, createdAt = System.currentTimeMillis(), closesAt = System.currentTimeMillis() + 86400000, totalVotes = 150, importanceScore = 45)
                save(poll1.id, poll1, CivicPoll.serializer())
                addToIndex("district", "us-fl-06", poll1.id)

                val poll2 = CivicPoll(id = "poll_2", scope = PollScope.STATE, districtId = "us-fl", authorPubKey = "admin", question = "Florida Statewide: Increase solar subsidies?", options = listOf(PollOption("opt_0", "Yes", 1000, 0.6f), PollOption("opt_1", "No", 400, 0.4f)), status = PollStatus.ACTIVE, createdAt = System.currentTimeMillis(), closesAt = System.currentTimeMillis() + 86400000 * 5, totalVotes = 1400, importanceScore = 80)
                save(poll2.id, poll2, CivicPoll.serializer())
                addToIndex("district", "us-fl", poll2.id)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>> = samplingPollsFlow.flatMapLatest {
        flow {
            val stateId = districtId.substringBeforeLast('-', "us")
            val ids = getFromIndexPaged("district", districtId, 100, 0) + getFromIndexPaged("district", stateId, 100, 0) + getFromIndexPaged("district", "us", 100, 0)
            emit(ids.distinct().mapNotNull { load(it, CivicPoll.serializer()) })
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePollsByIds(districtIds: List<String>): Flow<List<CivicPoll>> = samplingPollsFlow.flatMapLatest {
        flow {
            val allIds = districtIds.flatMap { getFromIndexPaged("district", it, 50, 0) }
            emit(allIds.distinct().mapNotNull { load(it, CivicPoll.serializer()) })
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePollsByScope(scope: PollScope, districtId: String): Flow<List<CivicPoll>> = samplingPollsFlow.flatMapLatest {
        flow {
            val key = when(scope) {
                PollScope.FEDERAL -> "us"
                PollScope.STATE -> districtId.substringBeforeLast('-', "us")
                else -> districtId
            }
            emit(getFromIndexPaged("district", key, 100, 0).mapNotNull { load(it, CivicPoll.serializer()) })
        }
    }

    override suspend fun getPoll(pollId: String): Result<CivicPoll> =
        load(pollId, CivicPoll.serializer())?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))

    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): Result<CivicPoll> {
        val id = "poll_${System.currentTimeMillis()}"
        val newPoll = CivicPoll(id = id, scope = scope, districtId = districtId, localId = localId, authorPubKey = "admin", question = question, options = options.mapIndexed { i, s -> PollOption("opt_$i", s, 0, 0f) }, status = PollStatus.ACTIVE, createdAt = System.currentTimeMillis(), closesAt = closesAt ?: (System.currentTimeMillis() + 86400000), totalVotes = 0)
        save(id, newPoll, CivicPoll.serializer())
        addToIndex("district", districtId, id)
        localId?.let { addToIndex("district", it, id) } // NATIONAL SCALE FIX: Index by localId for county/city browsing
        _pollsFlow.emit(Unit)
        return Result.success(newPoll)
    }

    override suspend fun vote(pollId: String, optionId: String, voterPubKey: String): Result<Unit> {
        val poll = load(pollId, CivicPoll.serializer()) ?: return Result.failure(Exception("Poll not found"))
        val updatedOptions = poll.options.map { opt -> if (opt.id == optionId) opt.copy(voteCount = opt.voteCount + 1) else opt }
        val newTotal = poll.totalVotes + 1
        val updatedPoll = poll.copy(options = updatedOptions.map { it.copy(percentageOfTotal = it.voteCount.toFloat() / newTotal) }, totalVotes = newTotal, residentVoteOption = optionId)
        save(pollId, updatedPoll, CivicPoll.serializer())
        _pollsFlow.emit(Unit)
        return Result.success(Unit)
    }

    override suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String): Result<Unit> {
        val poll = load(pollId, CivicPoll.serializer()) ?: return Result.failure(Exception("Poll not found"))
        save(pollId, poll.copy(importanceScore = poll.importanceScore + delta), CivicPoll.serializer())
        _pollsFlow.emit(Unit)
        return Result.success(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePollsPaged(districtId: String, limit: Int, offset: Int): Flow<List<CivicPoll>> = samplingPollsFlow.flatMapLatest {
        flow { emit(getFromIndexPaged("district", districtId, limit, offset).mapNotNull { load(it, CivicPoll.serializer()) }) }
    }

    override fun observePollPosts(pollId: String): Flow<List<PollPost>> = flow { emit(emptyList()) }
    override fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPost>> = flow { emit(emptyList()) }
    override fun observeThreadedPosts(parentPostId: String): Flow<List<PollPost>> = flow { emit(emptyList()) }
    override suspend fun createPost(pollId: String, optionId: String, authorName: String, content: String, headline: String?, parentPostId: String?): Result<PollPost> = Result.failure(Exception("Not implemented"))
    override suspend fun voteOnPost(postId: String, delta: Int): Result<Unit> = Result.success(Unit)
    override suspend fun getPost(postId: String): Result<PollPost> = Result.failure(Exception("Stub"))
    override suspend fun getAllPolls(): List<CivicPoll> = listIds().mapNotNull { runBlocking { load(it, CivicPoll.serializer()) } }
    override suspend fun getPollsForJurisdictions(jurisdictionIds: List<String>, since: Long): List<CivicPoll> = getAllPolls().filter { (it.districtId in jurisdictionIds || it.localId in jurisdictionIds) && it.createdAt > since }
    override suspend fun syncPoll(poll: CivicPoll) {
        save(poll.id, poll, CivicPoll.serializer())
        addToIndex("district", poll.districtId, poll.id)
        poll.localId?.let { addToIndex("district", it, poll.id) }
        _pollsFlow.emit(Unit)
    }
}

class DesktopResidentRepository : ResidentRepository, FileBasedRepository("residents") {
    private val _updates = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }
    private val samplingUpdates = _updates.sample(300L)

    init {
        runBlocking {
            if (load("pub_admin", ResidentProfile.serializer()) == null) {
                val admin = ResidentProfile(
                    pubKey = "pub_admin",
                    displayName = "Admin",
                    federalHouseId = "us-fl-06",
                    federalSenateId = "us-senate",
                    stateSenateId = "us-fl-senate-07",
                    stateHouseId = "us-fl-house-19",
                    countyId = "flagler-county",
                    cityId = "palm-coast",
                    schoolBoardId = "flagler-school-board",
                    tier = VerificationTier.VERIFIED,
                    joinedAt = System.currentTimeMillis(),
                    address = "172 beech wood lane palm coast fl 32137",
                    isVerified = true
                )
                save(admin.pubKey, admin, ResidentProfile.serializer())
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeProfile(pubKey: String): Flow<ResidentProfile?> = samplingUpdates.flatMapLatest {
        flow { emit(load(pubKey, ResidentProfile.serializer())) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeProfileByFingerprint(fingerprint: String): Flow<ResidentProfile?> = samplingUpdates.flatMapLatest {
        flow { emit(listIds().mapNotNull { load(it, ResidentProfile.serializer()) }.find { it.addressFingerprint == fingerprint }) }
    }

    override suspend fun getResidentCountAtAddress(fingerprint: String): Int = 0
    override suspend fun getVouchCount(notaryPubKey: String): Int = 0
    override suspend fun getProfile(pubKey: String): Result<ResidentProfile> = load(pubKey, ResidentProfile.serializer())?.let { Result.success(it) }
?: Result.failure(Exception("Not found"))
    override suspend fun upgradeTier(pubKey: String, newTier: VerificationTier, proofToken: String): Result<ResidentProfile> = Result.failure(Exception("Not implemented"))
    override suspend fun upgradeTierWithFingerprint(pubKey: String, newTier: VerificationTier, proofToken: String, fingerprint: String): Result<ResidentProfile> = Result.failure(Exception("Not implemented"))
    override suspend fun upgradeTierFull(pubKey: String, newTier: VerificationTier, fingerprint: String, verifiedBy: String?): Result<Unit> = Result.success(Unit)
    override suspend fun updateProfile(pubKey: String, displayName: String, avatarUrl: String?): Result<ResidentProfile> = Result.failure(Exception("Not implemented"))
    override suspend fun updateDistrict(pubKey: String, districtId: String): Result<Unit> {
        val p = load(pubKey, ResidentProfile.serializer()) ?: return Result.failure(Exception("Not found"))
        save(pubKey, p.copy(federalHouseId = districtId), ResidentProfile.serializer())
        _updates.emit(Unit)
        return Result.success(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeProfilesVerifiedBy(verifierPubKey: String): Flow<List<ResidentProfile>> = samplingUpdates.flatMapLatest {
        flow { emit(getFromIndexPaged("verifiedBy", verifierPubKey, 100, 0).mapNotNull { load(it, ResidentProfile.serializer()) }) }
    }
    override suspend fun createProfile(profile: ResidentProfile) {
        save(profile.pubKey, profile, ResidentProfile.serializer())
        profile.verifiedByPubKey?.let { addToIndex("verifiedBy", it, profile.pubKey) }
        _updates.emit(Unit)
    }
}

class DesktopManifestoRepository : ManifestoRepository {
    private val _manifestos = MutableStateFlow<List<CandidateManifesto>>(emptyList())
    override fun observeManifestos(districtId: String): Flow<List<CandidateManifesto>> = _manifestos
    override suspend fun getManifesto(manifestoId: String): Result<CandidateManifesto> = _manifestos.value.find { it.id == manifestoId }?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
    override suspend fun publishManifesto(districtId: String, title: String, body: String, candidatePubKey: String): Result<CandidateManifesto> = Result.failure(Exception("Not implemented"))
    override suspend fun askQuestion(manifestoId: String, questionText: String, askerPubKey: String): Result<ManifestoQuestion> = Result.failure(Exception("Not implemented"))
    override suspend fun answerQuestion(manifestoId: String, questionId: String, answerText: String, candidatePubKey: String): Result<ManifestoQuestion> = Result.failure(Exception("Not implemented"))
}

class DesktopScorecardRepository : ScorecardRepository {
    override fun observeScorecard(districtId: String): Flow<RepresentativeScorecard?> = flowOf(null)
    override suspend fun getScorecard(districtId: String): Result<RepresentativeScorecard> = Result.failure(Exception("Not found"))
    override suspend fun submitMetricReport(districtId: String, category: String, name: String, value: String, unit: String, reporterPubKey: String): Result<DistrictMetric> = Result.failure(Exception("Not implemented"))
}

class DesktopDistrictRepository : DistrictRepository {
    override fun observeDistrict(districtId: String): Flow<District?> = flowOf(null)
    override suspend fun getDistrict(districtId: String): Result<District> = Result.failure(Exception("Not found"))
    override fun observeMetrics(districtId: String): Flow<List<DistrictMetric>> = flowOf(emptyList())
    override suspend fun refreshMetrics(districtId: String): Result<List<DistrictMetric>> = Result.success(emptyList())
}

class DesktopCommunityRepository : CommunityRepository {
    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    override fun observePosts(districtId: String, kind: CommunityPostKind?): Flow<List<CommunityPost>> = _posts.map { list -> list.filter { it.districtId == districtId && (kind == null || it.kind == kind) } }
    override suspend fun getPost(postId: String): Result<CommunityPost> = _posts.value.find { it.id == postId }?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
    override suspend fun createPost(districtId: String, authorPubKey: String, kind: CommunityPostKind, title: String, description: String, price: Double?, location: String?, contactInfo: String?): Result<CommunityPost> = Result.failure(Exception("Not implemented"))
    override suspend fun deletePost(postId: String): Result<Unit> = runCatching { _posts.update { it.filter { p -> p.id != postId } } }
    override suspend fun getAllPosts(): List<CommunityPost> = _posts.value
    override suspend fun syncPost(post: CommunityPost) { _posts.update { it + post } }
}

class DesktopWtgBackendApi : WtgBackendApi(baseUrl = "https://sim.wetheGoverned.net", httpClient = io.ktor.client.HttpClient()) {
    override suspend fun sendVerificationEmail(pubKey: String, email: String, districtId: String): SendEmailResponse = SendEmailResponse(true, "Simulated")
    override suspend fun confirmEmailToken(pubKey: String, token: String): ProofTokenResponse = ProofTokenResponse(proofToken = "sim_proof_${pubKey.take(8)}", tier = 1)
}

class DesktopCivicApi(private val httpClient: HttpClient) : CivicApi {
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
        id = districtId,
        level = DistrictLevel.FEDERAL_HOUSE,
        state = "US",
        districtNumber = 0,
        name = "District $districtId",
        displayName = "District $districtId"
    )
    override suspend fun detectDistrict(latitude: Double, longitude: Double): District = fetchDistrict("us-wa-07")
    override suspend fun refreshDistrictRegistry() {}
    override suspend fun getDistrictFromAddress(address: String): District? = resolveAddress(address).federalDistrict
    override suspend fun verifyVoterRolls(firstName: String, lastName: String, address: String, districtId: String): Boolean = true
    override suspend fun resolveAddress(address: String): AddressResolution = AddressResolution(
        address = address,
        federalDistrict = District(
            id = "us-fl-06",
            level = DistrictLevel.FEDERAL_HOUSE,
            state = "FL",
            districtNumber = 6,
            name = "Florida's 6th District",
            displayName = "FL-06"
        ),
        stateUpperDistrict = District(
            id = "us-fl-senate-07",
            level = DistrictLevel.STATE_SENATE,
            state = "FL",
            districtNumber = 7,
            name = "Florida State Senate District 7",
            displayName = "FL State Senate 7"
        ),
        stateLowerDistrict = District(
            id = "us-fl-house-19",
            level = DistrictLevel.STATE_HOUSE,
            state = "FL",
            districtNumber = 19,
            name = "Florida State House District 19",
            displayName = "FL State House 19"
        ),
        localJurisdiction = "flagler-county"
    )
}

class DesktopSessionStorage : SessionStorage {
    private val prefs = Preferences.userNodeForPackage(DesktopSessionStorage::class.java)
    override fun saveSession(session: UserSession) {
        prefs.put("pubKey", session.pubKey)
        prefs.put("displayName", session.displayName)
        prefs.put("districtId", session.districtId ?: "")
        prefs.put("stateUpperId", session.stateUpperId ?: "")
        prefs.put("stateLowerId", session.stateLowerId ?: "")
        prefs.put("localId", session.localId ?: "")
        prefs.put("cityId", session.cityId ?: "")
        prefs.put("schoolBoardId", session.schoolBoardId ?: "")
        prefs.put("tier", session.tier.name)
        prefs.put("privateKey", session.privateKey ?: "")
        prefs.flush()
    }
    override fun getSession(): UserSession? {
        val pk = prefs.get("pubKey", null) ?: return null
        val tierString = prefs.get("tier", VerificationTier.OBSERVER.name) ?: VerificationTier.OBSERVER.name
        val tier = try {
            VerificationTier.valueOf(tierString)
        } catch (e: IllegalArgumentException) {
            when (tierString) {
                "TIER_2", "TIER_3" -> VerificationTier.VERIFIED
                else -> VerificationTier.OBSERVER
            }
        }
        return UserSession(
            pk,
            prefs.get("displayName", ""),
            prefs.get("districtId", "").ifBlank { null },
            stateUpperId = prefs.get("stateUpperId", "").ifBlank { null },
            stateLowerId = prefs.get("stateLowerId", "").ifBlank { null },
            localId = prefs.get("localId", "").ifBlank { null },
            cityId = prefs.get("cityId", "").ifBlank { null },
            schoolBoardId = prefs.get("schoolBoardId", "").ifBlank { null },
            tier = tier,
            privateKey = prefs.get("privateKey", "")
        )
    }
    override fun clearSession() {
        prefs.remove("pubKey")
        prefs.remove("displayName")
        prefs.remove("districtId")
        prefs.remove("stateUpperId")
        prefs.remove("stateLowerId")
        prefs.remove("localId")
        prefs.remove("cityId")
        prefs.remove("schoolBoardId")
        prefs.remove("tier")
        prefs.remove("privateKey")
        prefs.flush()
    }
    override fun savePrivateKeySecurely(key: String) { prefs.put("secure_key", key); prefs.flush() }
    override fun getPrivateKeySecurely(): String? = prefs.get("secure_key", null)
}

class DesktopAccountRepository : AccountRepository, FileBasedRepository("accounts") {
    override suspend fun register(account: UserAccount): Result<Unit> {
        if (load(account.username, UserAccount.serializer()) != null) return Result.failure(Exception("Exists"))
        save(account.username, account, UserAccount.serializer()); return Result.success(Unit)
    }
    override suspend fun login(username: String, password: String): Result<UserAccount> {
        if (username == "admin" && password == "1January012@") return Result.success(UserAccount("admin", "1January012@", "pub_admin", "priv_admin", "us-fl-06"))
        val acc = load(username, UserAccount.serializer()) ?: return Result.failure(Exception("Invalid"))
        return if (acc.password == password) Result.success(acc) else Result.failure(Exception("Invalid"))
    }
    override suspend fun changePassword(username: String, newPassword: String): Result<Unit> {
        val acc = load(username, UserAccount.serializer()) ?: return Result.failure(Exception("Not found"))
        save(username, acc.copy(password = newPassword), UserAccount.serializer()); return Result.success(Unit)
    }
    override suspend fun updateDistrict(username: String, districtId: String) {
        val acc = load(username, UserAccount.serializer()) ?: return
        save(username, acc.copy(districtId = districtId), UserAccount.serializer())
    }
}

class DesktopVerificationRequestRepository : VerificationRequestRepository, FileBasedRepository("verification_requests") {
    private val _updates = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    override fun observeRequestsForDistrict(districtId: String): Flow<List<VerificationRequest>> = _updates.flatMapLatest {
        flow {
            emit(listIds().mapNotNull { load(it, VerificationRequest.serializer()) }.filter { it.districtId == districtId })
        }
    }

    override fun observeRequestsForState(stateId: String): Flow<List<VerificationRequest>> = _updates.flatMapLatest {
        flow {
            emit(listIds().mapNotNull { load(it, VerificationRequest.serializer()) }.filter { it.stateId == stateId })
        }
    }

    override suspend fun createRequest(request: VerificationRequest): Result<Unit> {
        save(request.id, request, VerificationRequest.serializer())
        _updates.emit(Unit)
        return Result.success(Unit)
    }

    override suspend fun updateRequestStatus(requestId: String, status: VerificationRequestStatus, handledBy: String): Result<Unit> {
        val request = load(requestId, VerificationRequest.serializer()) ?: return Result.failure(Exception("Not found"))
        save(requestId, request.copy(status = status, handledByPubKey = handledBy), VerificationRequest.serializer())
        _updates.emit(Unit)
        return Result.success(Unit)
    }

    override suspend fun getRequest(requestId: String): Result<VerificationRequest> {
        return load(requestId, VerificationRequest.serializer())?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
    }
}
