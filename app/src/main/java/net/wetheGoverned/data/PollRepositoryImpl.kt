package net.wetheGoverned.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import net.wetheGoverned.local.dao.PollDao
import net.wetheGoverned.local.entity.DistrictPollEntity
import net.wetheGoverned.model.*
import net.wetheGoverned.remote.api.CivicApi
import net.wetheGoverned.core.CivicPublisher
import net.wetheGoverned.core.DispatcherProvider
import javax.inject.Inject

class PollRepositoryImpl @Inject constructor(
    private val pollDao: PollDao,
    private val civicApi: CivicApi,
    private val civicPublisher: CivicPublisher,
    private val dispatchers: DispatcherProvider,
) : PollRepository {

    override fun observeDistrictPolls(districtId: String): Flow<List<DistrictPoll>> =
        pollDao.observePolls(districtId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getPoll(pollId: String): Result<DistrictPoll> =
        withContext(dispatchers.io()) {
            runCatching {
                pollDao.getPoll(pollId)?.toDomain()
                    ?: civicApi.fetchPoll(pollId).also { remote ->
                        pollDao.upsertPoll(remote.toEntity())
                    }
            }
        }

    override suspend fun createPoll(
        districtId: String,
        question: String,
        options: List<String>,
        closesAt: Long?,
    ): Result<DistrictPoll> = withContext(dispatchers.io()) {
        runCatching {
            val poll = civicApi.createPoll(districtId, question, options, closesAt)
            pollDao.upsertPoll(poll.toEntity())
            poll
        }
    }

    override suspend fun vote(
        pollId: String,
        optionId: String,
        voterPubKey: String,
    ): Result<Unit> = withContext(dispatchers.io()) {
        pollDao.applyOptimisticVote(pollId, optionId)
        runCatching {
            civicPublisher.signPublishImportCivicEvent(
                kind = CivicEventKind.POLL_VOTE,
                tags = listOf("poll_id:$pollId", "option_id:$optionId"),
                content = "",
                pubKey = voterPubKey,
            )
        }.onFailure {
            pollDao.revertOptimisticVote(pollId)
        }
    }

    private fun DistrictPollEntity.toDomain(): DistrictPoll {
        return DistrictPoll(
            id = id,
            districtId = districtId,
            authorPubKey = authorPubKey,
            question = question,
            options = emptyList(), // TODO: parse optionsJson
            status = PollStatus.valueOf(status),
            createdAt = createdAt,
            closesAt = closesAt,
            totalVotes = totalVotes,
            residentVoteOption = residentVoteOption,
        )
    }

    private fun DistrictPoll.toEntity() = DistrictPollEntity(
        id = id,
        districtId = districtId,
        authorPubKey = authorPubKey,
        question = question,
        optionsJson = "[]",
        status = status.name,
        createdAt = createdAt,
        closesAt = closesAt,
        totalVotes = totalVotes,
        residentVoteOption = residentVoteOption,
        cachedAt = System.currentTimeMillis(),
    )
}
