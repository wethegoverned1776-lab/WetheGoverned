package net.wetheGoverned.data.repository

import net.wetheGoverned.model.DistrictPoll
import net.wetheGoverned.model.PollOption
import net.wetheGoverned.model.PollStatus
import java.util.UUID
import javax.inject.Inject

class PollRepositoryImpl @Inject constructor() : PollRepository {

    // In-memory stub — replace with Room + Nostr relay (kind 30_100/30_101) later
    private val polls = mutableListOf<DistrictPoll>()

    override suspend fun getPolls(): List<DistrictPoll> = polls.toList()

    override suspend fun getPoll(pollId: String): DistrictPoll? =
        polls.find { it.id == pollId }

    override suspend fun createPoll(question: String, options: List<String>): DistrictPoll {
        val poll = DistrictPoll(
            id = UUID.randomUUID().toString(),
            question = question,
            options = options.mapIndexed { i, label ->
                PollOption(
                    id = "opt_$i",
                    label = label,
                    voteCount = 0,
                    percentageOfTotal = 0f
                )
            },
            status = PollStatus.ACTIVE,
            totalVotes = 0,
            residentVoteOption = null,
            closesAt = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L // 7 days
        )
        polls.add(poll)
        return poll
    }

    override suspend fun submitVote(pollId: String, optionId: String) {
        val index = polls.indexOfFirst { it.id == pollId }
        if (index == -1) return
        val poll = polls[index]
        val newOptions = poll.options.map { opt ->
            if (opt.id == optionId) opt.copy(voteCount = opt.voteCount + 1) else opt
        }
        val total = newOptions.sumOf { it.voteCount }.toFloat().coerceAtLeast(1f)
        polls[index] = poll.copy(
            options = newOptions.map { it.copy(percentageOfTotal = it.voteCount / total) },
            totalVotes = newOptions.sumOf { it.voteCount },
            residentVoteOption = optionId
        )
    }
}