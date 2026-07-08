package net.wetheGoverned.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.PollRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollRepositoryImpl @Inject constructor() : PollRepository {
    override fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>> = flowOf(emptyList())
    override fun observePollsByIds(districtIds: List<String>): Flow<List<CivicPoll>> = flowOf(emptyList())
    override fun observePollsByScope(scope: PollScope, districtId: String): Flow<List<CivicPoll>> = flowOf(emptyList())
    override suspend fun getPoll(pollId: String): Result<CivicPoll> = Result.failure(Exception("Stub"))
    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): Result<CivicPoll> = Result.failure(Exception("Stub"))
    override suspend fun vote(pollId: String, optionId: String, voterPubKey: String): Result<Unit> = Result.success(Unit)
    override suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String): Result<Unit> = Result.success(Unit)
    override fun observePollsPaged(districtId: String, limit: Int, offset: Int): Flow<List<CivicPoll>> = flowOf(emptyList())
    override fun observePollPosts(pollId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override fun observeThreadedPosts(parentPostId: String): Flow<List<PollPost>> = flowOf(emptyList())
    override suspend fun createPost(pollId: String, optionId: String, authorName: String, content: String, headline: String?, parentPostId: String?): Result<PollPost> = Result.failure(Exception("Stub"))
    override suspend fun voteOnPost(postId: String, delta: Int): Result<Unit> = Result.success(Unit)
    override suspend fun getPost(postId: String): Result<PollPost> = Result.failure(Exception("Stub"))
    override suspend fun getAllPolls(): List<CivicPoll> = emptyList()
    override suspend fun getPollsForJurisdictions(jurisdictionIds: List<String>, since: Long): List<CivicPoll> = emptyList()
    override suspend fun syncPoll(poll: CivicPoll) {}
}
