package net.wetheGoverned.remote.impl

import net.wetheGoverned.model.*
import net.wetheGoverned.remote.api.CivicApi
import javax.inject.Inject
import java.util.UUID

class CivicApiImpl @Inject constructor(
    private val congressApiKey: String,
    private val openStatesApiKey: String,
    private val googleCivicApiKey: String,
    private val relayUrls: List<String>,
) : CivicApi {

    override suspend fun fetchPolls(districtId: String, limit: Int, before: Long?): List<CivicPoll> {
        return listOf(
            CivicPoll(
                id = "poll-1",
                scope = PollScope.DISTRICT,
                districtId = districtId,
                authorPubKey = "system",
                question = "Should the district support the A1A reinforcement bill?",
                options = listOf(
                    PollOption("opt_0", "Yes, immediate action", 120, 0.8f),
                    PollOption("opt_1", "No, too expensive", 30, 0.2f)
                ),
                status = PollStatus.ACTIVE,
                createdAt = System.currentTimeMillis(),
                closesAt = System.currentTimeMillis() + 86400000,
                totalVotes = 150,
                importanceScore = 45
            )
        )
    }

    override suspend fun fetchPoll(pollId: String): CivicPoll {
        return fetchPolls("us-fl-06").first()
    }

    override suspend fun createPoll(
        districtId: String, 
        question: String, 
        options: List<String>, 
        closesAt: Long?, 
        scope: PollScope, 
        localId: String?
    ): CivicPoll {
        return CivicPoll(
            id = UUID.randomUUID().toString(),
            scope = scope,
            districtId = districtId,
            localId = localId,
            authorPubKey = "me",
            question = question,
            options = options.mapIndexed { i, l -> PollOption("opt_$i", l, 0, 0f) },
            status = PollStatus.ACTIVE,
            createdAt = System.currentTimeMillis(),
            closesAt = closesAt,
            totalVotes = 0
        )
    }

    override suspend fun getRepresentativeVote(legislationId: String): String? {
        return "opt_0" 
    }

    override suspend fun fetchScorecard(districtId: String): RepresentativeScorecard {
        return RepresentativeScorecard(
            representativePubKey = "rep-123",
            districtId = districtId,
            name = "Michael Waltz",
            party = "Republican",
            overallScore = 92,
            categories = emptyList(),
            lastUpdated = System.currentTimeMillis()
        )
    }

    override suspend fun fetchMetrics(districtId: String): List<DistrictMetric> = emptyList()
    override suspend fun fetchManifestos(districtId: String): List<CandidateManifesto> = emptyList()
    override suspend fun fetchManifesto(manifestoId: String): CandidateManifesto = TODO()
    
    override suspend fun fetchProfile(pubKey: String): ResidentProfile {
        return ResidentProfile(
            pubKey = pubKey,
            displayName = "User",
            firstName = null,
            lastName = null,
            districtId = "us-fl-06",
            localId = null,
            tier = VerificationTier.UNVERIFIED,
            avatarUrl = null,
            bio = null,
            joinedAt = System.currentTimeMillis(),
            addressFingerprint = null,
            verifiedByPubKey = null
        )
    }
    
    override suspend fun upgradeTier(pubKey: String, proofToken: String, targetTier: VerificationTier): ResidentProfile {
        return fetchProfile(pubKey).copy(tier = targetTier)
    }
    
    override suspend fun fetchDistrict(districtId: String): District {
        return District(districtId, "FL", 6, "Florida District 6")
    }

    override suspend fun detectDistrict(latitude: Double, longitude: Double): District {
        return fetchDistrict("us-fl-06")
    }

    override suspend fun getDistrictFromAddress(address: String): District? {
        val normalizedAddress = address.lowercase()
        return if (normalizedAddress.contains("fl") || normalizedAddress.contains("florida") || normalizedAddress.contains("32") || normalizedAddress.contains("33")) {
            District("us-fl-06", "FL", 6, "Florida District 6")
        } else if (normalizedAddress.contains("ca") || normalizedAddress.contains("california") || normalizedAddress.contains("9")) {
            District("us-ca-01", "CA", 1, "California District 1")
        } else {
            null
        }
    }

    override suspend fun verifyVoterRolls(
        firstName: String,
        lastName: String,
        address: String,
        districtId: String
    ): Boolean {
        val district = getDistrictFromAddress(address)
        return district != null && district.id.equals(districtId, ignoreCase = true) && firstName.isNotBlank() && lastName.isNotBlank()
    }
}
