package net.wetheGoverned.remote.impl

import net.wetheGoverned.model.*
import net.wetheGoverned.remote.api.CivicApi
import javax.inject.Inject

class CivicApiImpl @Inject constructor(
    private val congressApiKey: String,
    private val openStatesApiKey: String,
    private val googleCivicApiKey: String,
    private val relayUrl: String,
) : CivicApi {

    override suspend fun fetchPolls(districtId: String, limit: Int, before: Long?): List<DistrictPoll> = emptyList()
    override suspend fun fetchPoll(pollId: String): DistrictPoll = TODO()
    override suspend fun createPoll(districtId: String, question: String, options: List<String>, closesAt: Long?): DistrictPoll = TODO()
    override suspend fun fetchScorecard(districtId: String): RepresentativeScorecard = TODO()
    override suspend fun fetchMetrics(districtId: String): List<DistrictMetric> = emptyList()
    override suspend fun fetchManifestos(districtId: String): List<CandidateManifesto> = emptyList()
    override suspend fun fetchManifesto(manifestoId: String): CandidateManifesto = TODO()
    override suspend fun fetchProfile(pubKey: String): ResidentProfile = TODO()
    override suspend fun upgradeTier(pubKey: String, proofToken: String, targetTier: VerificationTier): ResidentProfile = TODO()
    override suspend fun fetchDistrict(districtId: String): District = TODO()
    override suspend fun detectDistrict(latitude: Double, longitude: Double): District = TODO()
}
