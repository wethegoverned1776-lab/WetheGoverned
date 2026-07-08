package net.wetheGoverned.model

import kotlinx.serialization.Serializable

@Serializable
data class CivicPoll(
    val id: String,
    val scope: CivicScope,
    val districtId: String,
    val localId: String? = null,
    val authorPubKey: String,
    val question: String,
    val options: List<PollOption>,
    val status: PollStatus,
    val createdAt: Long,
    val closesAt: Long?,
    val totalVotes: Int,
    val importanceScore: Int = 0,
    val userImportanceVote: Int = 0,
    val residentVoteOption: String? = null,
    val linkedLegislationId: String? = null,
    val districtBreakdown: Map<String, Map<String, Int>> = emptyMap(),
    val cachedWeightedResults: Map<String, Float> = emptyMap() // ERR_016 FIX: Cache heavy results
) {
    fun calculateWeightedResults(): Map<String, Float> {
        if (cachedWeightedResults.isNotEmpty()) return cachedWeightedResults
        if (scope != CivicScope.STATE && scope != CivicScope.FEDERAL) return emptyMap()
        // ... (remaining logic)
        if (districtBreakdown.isEmpty()) return emptyMap()

        val optionResults = mutableMapOf<String, Float>()
        val totalDistricts = districtBreakdown.size

        options.forEach { option ->
            var sumOfPercentages = 0f
            districtBreakdown.values.forEach { districtVotes ->
                val totalDistrictVotes = districtVotes.values.sum()
                if (totalDistrictVotes > 0) {
                    val support = (districtVotes[option.id] ?: 0).toFloat() / totalDistrictVotes
                    sumOfPercentages += support
                }
            }
            optionResults[option.id] = if (totalDistricts > 0) sumOfPercentages / totalDistricts else 0f
        }
        return optionResults
    }
}

@Serializable
data class PollOption(
    val id: String,
    val label: String,
    val voteCount: Int,
    val percentageOfTotal: Float,
    val vectorClock: Map<String, Long> = emptyMap() // ERR_X10 FIX: CRDT Support
)
