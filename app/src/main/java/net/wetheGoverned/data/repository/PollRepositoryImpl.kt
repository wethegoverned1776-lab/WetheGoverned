package net.wetheGoverned.repository

import kotlinx.coroutines.flow.*
import net.wetheGoverned.data.CivicConverters
import net.wetheGoverned.local.dao.PollDao
import net.wetheGoverned.local.dao.PollPostDao
import net.wetheGoverned.local.entity.DistrictPollEntity
import net.wetheGoverned.local.entity.PollPostEntity
import net.wetheGoverned.model.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PollRepositoryImpl @Inject constructor(
    private val pollDao: PollDao,
    private val pollPostDao: PollPostDao
) : PollRepository {

    private val converters = CivicConverters()

    override fun observeDistrictPolls(districtId: String): Flow<List<CivicPoll>> {
        return pollDao.observePolls(districtId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPoll(pollId: String): Result<CivicPoll> {
        val entity = pollDao.getPoll(pollId)
        return if (entity != null) Result.success(entity.toDomain()) else Result.failure(Exception("Poll not found"))
    }

    suspend fun getDistrictPollsSync(districtId: String): List<CivicPoll> {
        return pollDao.getPollsSync(districtId).map { it.toDomain() }
    }

    override suspend fun createPoll(
        districtId: String,
        question: String,
        options: List<String>,
        closesAt: Long?,
        scope: PollScope,
        localId: String?
    ): Result<CivicPoll> {
        val poll = CivicPoll(
            id = UUID.randomUUID().toString(),
            scope = scope,
            districtId = districtId,
            localId = localId,
            authorPubKey = "local-user",
            question = question,
            options = options.mapIndexed { index, label ->
                PollOption(
                    id = "opt_$index",
                    label = label,
                    voteCount = 0,
                    percentageOfTotal = 0f
                )
            },
            status = PollStatus.ACTIVE,
            createdAt = System.currentTimeMillis(),
            closesAt = closesAt ?: (System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L),
            totalVotes = 0,
            residentVoteOption = null
        )

        pollDao.upsertPoll(poll.toEntity())
        return Result.success(poll)
    }

    override suspend fun vote(
        pollId: String,
        optionId: String,
        voterPubKey: String
    ): Result<Unit> {
        val poll = pollDao.getPoll(pollId)?.toDomain() ?: return Result.failure(Exception("Poll not found"))
        
        if (poll.residentVoteOption == null) {
            val updatedOptions = poll.options.map { option ->
                if (option.id == optionId) {
                    option.copy(voteCount = option.voteCount + 1)
                } else {
                    option
                }
            }
            val newTotalVotes = poll.totalVotes + 1
            
            val finalOptions = updatedOptions.map { option ->
                option.copy(
                    percentageOfTotal = if (newTotalVotes > 0) {
                        option.voteCount.toFloat() / newTotalVotes.toFloat()
                    } else 0f
                )
            }

            val updatedPoll = poll.copy(
                options = finalOptions,
                totalVotes = newTotalVotes,
                residentVoteOption = optionId
            )
            
            pollDao.upsertPoll(updatedPoll.toEntity())
        }
        return Result.success(Unit)
    }

    override suspend fun voteImportance(pollId: String, delta: Int, voterPubKey: String): Result<Unit> {
        val poll = pollDao.getPoll(pollId)?.toDomain() ?: return Result.failure(Exception("Poll not found"))
        val newVote = if (poll.userImportanceVote == delta) 0 else delta
        val scoreChange = newVote - poll.userImportanceVote
        
        val updatedPoll = poll.copy(
            importanceScore = poll.importanceScore + scoreChange,
            userImportanceVote = newVote
        )
        pollDao.upsertPoll(updatedPoll.toEntity())
        return Result.success(Unit)
    }

    override fun observePollPosts(pollId: String): Flow<List<PollPost>> {
        return pollPostDao.getAllPostsForPoll(pollId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeOptionPosts(pollId: String, optionId: String): Flow<List<PollPost>> {
        return pollPostDao.observeOptionPosts(pollId, optionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeThreadedPosts(parentPostId: String): Flow<List<PollPost>> {
        return pollPostDao.observeThreadedPosts(parentPostId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createPost(
        pollId: String,
        optionId: String,
        authorName: String,
        content: String,
        headline: String?,
        parentPostId: String?
    ): Result<PollPost> {
        val newPost = PollPost(
            pollId = pollId,
            optionId = optionId,
            authorName = authorName,
            content = content,
            headline = headline,
            parentPostId = parentPostId
        )
        pollPostDao.upsertPost(newPost.toEntity())
        return Result.success(newPost)
    }

    override suspend fun voteOnPost(postId: String, delta: Int): Result<Unit> {
        val post = pollPostDao.getPost(postId)?.toDomain() ?: return Result.failure(Exception("Post not found"))
        
        val newVote = if (post.userVote == delta) 0 else delta
        val scoreChange = newVote - post.userVote
        
        pollPostDao.updateVote(postId, scoreChange, newVote)
        return Result.success(Unit)
    }

    override suspend fun getPost(postId: String): Result<PollPost> {
        val entity = pollPostDao.getPost(postId)
        return if (entity != null) Result.success(entity.toDomain()) else Result.failure(Exception("Post not found"))
    }

    // --- Helpers ---

    private fun DistrictPollEntity.toDomain() = CivicPoll(
        id = id,
        scope = PollScope.valueOf(scope ?: "DISTRICT"),
        districtId = districtId,
        localId = localId,
        authorPubKey = authorPubKey,
        question = question,
        options = converters.toPollOptions(optionsJson),
        status = PollStatus.valueOf(status),
        createdAt = createdAt,
        closesAt = closesAt,
        totalVotes = totalVotes,
        importanceScore = importanceScore,
        userImportanceVote = userImportanceVote,
        residentVoteOption = residentVoteOption,
        linkedLegislationId = linkedLegislationId
    )

    private fun CivicPoll.toEntity() = DistrictPollEntity(
        id = id,
        scope = scope.name,
        districtId = districtId,
        localId = localId,
        authorPubKey = authorPubKey,
        question = question,
        optionsJson = converters.fromPollOptions(options),
        status = status.name,
        createdAt = createdAt,
        closesAt = closesAt,
        totalVotes = totalVotes,
        importanceScore = importanceScore,
        userImportanceVote = userImportanceVote,
        residentVoteOption = residentVoteOption,
        linkedLegislationId = linkedLegislationId,
        cachedAt = System.currentTimeMillis()
    )

    private fun PollPostEntity.toDomain() = PollPost(
        id = id,
        pollId = pollId,
        optionId = optionId,
        parentPostId = parentPostId,
        headline = headline,
        authorName = authorName,
        content = content,
        score = score,
        userVote = userVote,
        createdAt = createdAt
    )

    private fun PollPost.toEntity() = PollPostEntity(
        id = id,
        pollId = pollId,
        optionId = optionId,
        parentPostId = parentPostId,
        headline = headline,
        authorName = authorName,
        content = content,
        score = score,
        userVote = userVote,
        createdAt = createdAt
    )
    
    // P2P Specific: Fetch data for sync (generalized from hardcoded district)
    suspend fun getAllPolls(districtId: String? = null): List<CivicPoll> {
        return if (districtId != null) {
            pollDao.getPollsSync(districtId).map { it.toDomain() }
        } else {
            // If no district specified, return all polls from all districts
            pollDao.getAllPollsAcrossDistricts().map { it.toDomain() }
        }
    }

    suspend fun getAllPosts(): List<PollPost> = pollPostDao.getAllPosts().map { it.toDomain() }
    
    // P2P Specific: Merge external data
    suspend fun syncPoll(poll: CivicPoll) = pollDao.upsertPoll(poll.toEntity())
    suspend fun syncPost(post: PollPost) = pollPostDao.upsertPost(post.toEntity())
}
