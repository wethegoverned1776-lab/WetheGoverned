package net.wetheGoverned.data

import net.wetheGoverned.model.*
import net.wetheGoverned.remote.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CivicApiImpl @Inject constructor() : CivicApi {
    override suspend fun fetchPolls(districtId: String, limit: Int, before: Long?): List<CivicPoll> = emptyList()
    override suspend fun fetchPoll(pollId: String): CivicPoll = throw Exception("Stub")
    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?, scope: PollScope, localId: String?): CivicPoll = throw Exception("Stub")
    override suspend fun getRepresentativeVote(legislationId: String): String? = null
    override suspend fun fetchScorecard(districtId: String): RepresentativeScorecard = throw Exception("Stub")
    override suspend fun fetchMetrics(districtId: String): List<DistrictMetric> = emptyList()
    override suspend fun fetchManifestos(districtId: String): List<CandidateManifesto> = emptyList()
    override suspend fun fetchManifesto(manifestoId: String): CandidateManifesto = throw Exception("Stub")
    override suspend fun fetchProfile(pubKey: String): ResidentProfile = throw Exception("Stub")
    override suspend fun upgradeTier(pubKey: String, proofToken: String, targetTier: VerificationTier): ResidentProfile = throw Exception("Stub")
    
    override suspend fun fetchDistrict(districtId: String): District = District(
        id = districtId,
        level = DistrictLevel.FEDERAL_HOUSE,
        state = "US",
        districtNumber = 0,
        name = "District $districtId",
        displayName = "District $districtId"
    )

    override suspend fun detectDistrict(latitude: Double, longitude: Double): District = fetchDistrict("us-fl-06")
    override suspend fun refreshDistrictRegistry() {}
    
    override suspend fun getDistrictFromAddress(address: String): District? = resolveAddress(address).federalDistrict
    
    override suspend fun verifyVoterRolls(firstName: String, lastName: String, address: String, districtId: String): Boolean = true
    
    override suspend fun resolveAddress(address: String): AddressResolution = AddressResolution(
        address = address,
        federalDistrict = District(
            id = "us-fl-06",
            level = DistrictLevel.FEDERAL_HOUSE,
            state = "FL",
            districtNumber = 6,
            name = "Florida's 6th District",
            displayName = "FL-06"
        ),
        stateUpperDistrict = District(
            id = "us-fl-senate-07",
            level = DistrictLevel.STATE_SENATE,
            state = "FL",
            districtNumber = 7,
            name = "Florida State Senate District 7",
            displayName = "FL State Senate 7"
        ),
        stateLowerDistrict = District(
            id = "us-fl-house-19",
            level = DistrictLevel.STATE_HOUSE,
            state = "FL",
            districtNumber = 19,
            name = "Florida State House District 19",
            displayName = "FL State House 19"
        ),
        localJurisdiction = "flagler-county"
    )
}
