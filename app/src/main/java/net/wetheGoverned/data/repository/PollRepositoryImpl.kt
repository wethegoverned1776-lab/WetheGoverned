package net.wetheGoverned.data.repository

import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.core.CivicPublisher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollRepositoryImpl @Inject constructor(
    private val publisher: CivicPublisher
) : PollRepository {
    private val _polls = MutableStateFlow<List<CivicPoll>>(emptyList())

    override fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>> = 
        _polls.map { list -> list.filter { it.districtId == districtId || it.districtId == "us" } }
    
    override fun observePollsByIds(districtIds: List<String>): Flow<List<CivicPoll>> = 
        _polls.map { list -> list.filter { it.districtId in districtIds } }
    
    override fun observePollsByScope(scope: PollScope, districtId: String): Flow<List<CivicPoll>> = 
        _polls.map { list -> list.filter { it.scope == scope } }

    override suspend fun getPoll(pollId: String): Result<CivicPoll> = 
        _polls.value.find { it.id == pollId }?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))

    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): Result<CivicPoll> {
        val id = "poll_${System.currentTimeMillis()}"
        val newPoll = CivicPoll(
            id = id, scope = scope, districtId = districtId, localId = localId,
            authorPubKey = "android_user", question = question,
            options = options.mapIndexed { i, s -> PollOption("opt_$i", s, 0, 0f) },
            status = PollStatus.ACTIVE, createdAt = System.currentTimeMillis(),
            closesAt = closesAt ?: (System.currentTimeMillis() + 86400000), totalVotes = 0
        )
        _polls.update { it + newPoll }
        publisher.signPublishImportCivicEvent(
            kind = when(scope) {
                PollScope.FEDERAL -> CivicEventKind.FEDERAL_POLL
                PollScope.STATE -> CivicEventKind.STATE_POLL
                else -> CivicEventKind.DISTRICT_POLL
            },
            tags = listOf("d", districtId),
            content = Json.encodeToString(CivicPoll.serializer(), newPoll),
            pubKey = "android_user"
        )
        return Result.success(newPoll)
    }

    override suspend fun vote(pollId: String, optionId: String, voterPubKey: String): Result<Unit> {
        val poll = _polls.value.find { it.id == pollId } ?: return Result.failure(Exception("Poll not found"))
        val updatedOptions = poll.options.map { opt -> if (opt.id == optionId) opt.copy(voteCount = opt.voteCount + 1) else opt }
        val newTotal = poll.totalVotes + 1
        val updatedPoll = poll.copy(options = updatedOptions.map { it.copy(percentageOfTotal = it.voteCount.toFloat() / newTotal) }, totalVotes = newTotal, residentVoteOption = optionId)
        _polls.update { it.map { p -> if (p.id == pollId) updatedPoll else p } }

        val vote = CivicVote(
            id = "vote_${pollId}_$voterPubKey",
            pollId = pollId,
            optionId = optionId,
            voterPubKey = voterPubKey,
            voterName = "Android Resident",
            timestamp = System.currentTimeMillis(),
            nonce = 0L,
            createdAt = System.currentTimeMillis()
        )
        publisher.signPublishImportCivicEvent(
            kind = CivicEventKind.POLL_VOTE,
            tags = listOf("d", pollId),
            content = Json.encodeToString(CivicVote.serializer(), vote),
            pubKey = voterPubKey
        )
        return Result.success(Unit)
    }

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
    
    override suspend fun syncPoll(poll: CivicPoll) {
        _polls.update { if (it.none { p -> p.id == poll.id }) it + poll else it }
    }
}
