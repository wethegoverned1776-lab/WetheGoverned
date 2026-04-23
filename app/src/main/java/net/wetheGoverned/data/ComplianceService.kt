package net.wetheGoverned.data

import net.wetheGoverned.model.DistrictPoll
import net.wetheGoverned.model.PollStatus
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.remote.api.CivicApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComplianceService @Inject constructor(
    private val pollRepository: PollRepository,
    private val civicApi: CivicApi
) {

    /**
     * Calculates the alignment score (0-100) between district polls and official votes.
     */
    suspend fun calculateAlignmentScore(districtId: String): Int {
        // 1. Fetch completed district polls
        // Use getPollsSync from the interface if available, or fetch via remote if needed.
        // For compliance, we likely need all historical data.
        val polls = pollRepository.observeDistrictPolls(districtId).let { 
            // In a real implementation we'd fetch a list, for now simulation
            emptyList<DistrictPoll>()
        }.filter { it.status == PollStatus.CLOSED && it.linkedLegislationId != null }

        if (polls.isEmpty()) return 100 // Default for new districts

        var matchedVotes = 0
        var totalVotesWithLegislation = 0

        for (poll in polls) {
            val officialVote = "Y" // Stub for logic demonstration
            
            totalVotesWithLegislation++
            // Compare poll winner with official vote
            val residentWinner = poll.options.maxByOrNull { it.voteCount }?.id
            if (residentWinner == officialVote) {
                matchedVotes++
            }
        }

        return if (totalVotesWithLegislation > 0) {
            (matchedVotes.toFloat() / totalVotesWithLegislation * 100).toInt()
        } else {
            100
        }
    }
}
