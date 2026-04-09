package net.wetheGoverned.data.repository

import net.wetheGoverned.model.DistrictPoll
import net.wetheGoverned.model.PollOption
import net.wetheGoverned.model.PollStatus

interface PollRepository {
    suspend fun getPolls(): List<DistrictPoll>
    suspend fun getPoll(pollId: String): DistrictPoll?
    suspend fun createPoll(question: String, options: List<String>): DistrictPoll
    suspend fun submitVote(pollId: String, optionId: String)
}