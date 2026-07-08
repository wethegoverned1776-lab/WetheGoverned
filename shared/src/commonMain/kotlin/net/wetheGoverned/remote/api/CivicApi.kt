package net.wetheGoverned.remote.api

import net.wetheGoverned.model.*

interface CivicApi {
    suspend fun fetchPolls(districtId: String, limit: Int = 20, before: Long? = null): List<CivicPoll>
    suspend fun fetchPoll(pollId: String): CivicPoll
    suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): CivicPoll
    
    suspend fun getRepresentativeVote(legislationId: String): String? 

    suspend fun fetchScorecard(districtId: String): RepresentativeScorecard
    suspend fun fetchMetrics(districtId: String): List<DistrictMetric>
    suspend fun fetchManifestos(districtId: String): List<CandidateManifesto>
    suspend fun fetchManifesto(manifestoId: String): CandidateManifesto
    suspend fun fetchProfile(pubKey: String): ResidentProfile
    suspend fun upgradeTier(pubKey: String, proofToken: String, targetTier: VerificationTier): ResidentProfile
    suspend fun fetchDistrict(districtId: String): District
    suspend fun detectDistrict(latitude: Double, longitude: Double): District
    
    suspend fun getDistrictFromAddress(address: String): District?

    /**
     * Resolves an address to multiple levels of government (Federal, State Upper, State Lower).
     * This uses a multi-source validation strategy.
     */
    suspend fun resolveAddress(address: String): AddressResolution

    suspend fun refreshDistrictRegistry()

    /**
     * Option A: Verify against official voter rolls.
     * In production, this calls a secure state/county database.
     */
    suspend fun verifyVoterRolls(
        firstName: String,
        lastName: String,
        address: String,
        districtId: String
    ): Boolean
}
