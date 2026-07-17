package net.wetheGoverned.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.PollRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollRepositoryImpl @Inject constructor() : PollRepository {
    private val mockPolls = listOf(
        CivicPoll(
            id = "poll_fed_1", 
            scope = PollScope.FEDERAL, 
            districtId = "us", 
            authorPubKey = "admin", 
            question = "Should the US implement term limits for Congress?", 
            options = listOf(PollOption("opt_1", "Yes, 12 years", 5000, 0.9f)), 
            status = PollStatus.ACTIVE, 
            createdAt = System.currentTimeMillis(), 
            closesAt = System.currentTimeMillis() + 86400000 * 30, 
            totalVotes = 5500
        ),
        CivicPoll(
            id = "poll_1", 
            scope = PollScope.DISTRICT, 
            districtId = "us-fl-06", 
            authorPubKey = "admin", 
            question = "Should the district support the A1A reinforcement bill?", 
            options = listOf(PollOption("opt_0", "Yes, immediate action", 120, 0.8f), PollOption("opt_1", "No, too expensive", 30, 0.2f)), 
            status = PollStatus.ACTIVE, 
            createdAt = System.currentTimeMillis(), 
            closesAt = System.currentTimeMillis() + 86400000, 
            totalVotes = 150, 
            importanceScore = 45
        ),
        CivicPoll(
            id = "poll_2", 
            scope = PollScope.STATE, 
            districtId = "us-fl", 
            authorPubKey = "admin", 
            question = "Florida Statewide: Increase solar subsidies?", 
            options = listOf(PollOption("opt_0", "Yes", 1000, 0.6f), PollOption("opt_1", "No", 400, 0.4f)), 
            status = PollStatus.ACTIVE, 
            createdAt = System.currentTimeMillis(), 
            closesAt = System.currentTimeMillis() + 86400000 * 5, 
            totalVotes = 1400, 
            importanceScore = 80
        )
    )

    override fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>> = 
        flowOf(mockPolls.filter { it.districtId == districtId || it.districtId == "us" })
    
    override fun observePollsByIds(districtIds: List<String>): Flow<List<CivicPoll>> = 
        flowOf(mockPolls.filter { it.districtId in districtIds })
    
    override fun observePollsByScope(scope: PollScope, districtId: String): Flow<List<CivicPoll>> = 
        flowOf(mockPolls.filter { it.scope == scope })

    override suspend fun getPoll(pollId: String): Result<CivicPoll> = 
        mockPolls.find { it.id == pollId }?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))

    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): Result<CivicPoll> = Result.failure(Exception("Stub"))
    override suspend fun vote(pollId: String, optionId: String, voterPubKey: String): Result<Unit> = Result.success(Unit)
    override suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String): Result<Unit> = Result.success(Unit)
    override fun observePollsPaged(districtId: String, limit: Int, offset: Int): Flow<List<CivicPoll>> = observeDistrictPolls(districtId)
    override fun observePollPosts(pollId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeThreadedPosts(parentPostId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override suspend fun createPost(pollId: String, optionId: String, authorName: String, content: String, headline: String?, parentPostId: String?): Result<PollPost> = Result.failure(Exception("Stub"))
    override suspend fun voteOnPost(postId: String, delta: Int): Result<Unit> = Result.success(Unit)
    override suspend fun getPost(postId: String): Result<PollPost> = Result.failure(Exception("Stub"))
    override suspend fun getAllPolls(): List<CivicPoll> = mockPolls
    override suspend fun getPollsForJurisdictions(jurisdictionIds: List<String>, since: Long): List<CivicPoll> = mockPolls.filter { it.districtId in jurisdictionIds }
    override suspend fun syncPoll(poll: CivicPoll) {}
}
