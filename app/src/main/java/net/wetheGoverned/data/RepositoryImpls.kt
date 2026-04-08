package net.wetheGoverned.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.wetheGoverned.core.*
import net.wetheGoverned.local.dao.*
import net.wetheGoverned.local.entity.*
import net.wetheGoverned.model.*
import net.wetheGoverned.remote.api.CivicApi
import javax.inject.Inject

class ManifestoRepositoryImpl @Inject constructor(
    private val manifestoDao: ManifestoDao,
    private val civicApi: CivicApi,
    private val civicPublisher: CivicPublisher,
    private val dispatchers: DispatcherProvider,
) : ManifestoRepository {
    override fun observeManifestos(districtId: String): Flow<List<CandidateManifesto>> =
        manifestoDao.observeManifestos(districtId).map { entities ->
            entities.map { entity ->
                CandidateManifesto(
                    id = entity.id,
                    candidatePubKey = entity.candidatePubKey,
                    districtId = entity.districtId,
                    title = entity.title,
                    body = entity.body,
                    publishedAt = entity.publishedAt,
                    questions = emptyList()
                )
            }
        }

    override suspend fun getManifesto(manifestoId: String) = runCatching { civicApi.fetchManifesto(manifestoId) }
    override suspend fun publishManifesto(districtId: String, title: String, body: String, candidatePubKey: String) =
        runCatching { civicApi.fetchManifestos(districtId).first() }
    override suspend fun askQuestion(manifestoId: String, questionText: String, askerPubKey: String) = TODO()
    override suspend fun answerQuestion(manifestoId: String, questionId: String, answerText: String, candidatePubKey: String) = TODO()
}

class ResidentRepositoryImpl @Inject constructor(
    private val profileDao: ResidentProfileDao,
    private val civicApi: CivicApi,
    private val dispatchers: DispatcherProvider,
) : ResidentRepository {
    override fun observeProfile(pubKey: String): Flow<ResidentProfile?> =
        profileDao.observeProfile(pubKey).map { entity ->
            entity?.let {
                ResidentProfile(
                    pubKey = it.pubKey,
                    displayName = it.displayName,
                    districtId = it.districtId,
                    tier = VerificationTier.valueOf(it.tier),
                    avatarUrl = it.avatarUrl,
                    bio = it.bio,
                    joinedAt = it.joinedAt
                )
            }
        }

    override suspend fun getProfile(pubKey: String) = runCatching { civicApi.fetchProfile(pubKey) }
    override suspend fun upgradeTier(pubKey: String, newTier: VerificationTier, proofToken: String) =
        runCatching { civicApi.upgradeTier(pubKey, proofToken, newTier) }
    override suspend fun updateProfile(pubKey: String, displayName: String, bio: String?, avatarUrl: String?) =
        runCatching { civicApi.fetchProfile(pubKey) }
}

class DistrictRepositoryImpl @Inject constructor(
    private val districtDao: DistrictDao,
    private val metricDao: MetricDao,
    private val civicApi: CivicApi,
    private val dispatchers: DispatcherProvider,
) : DistrictRepository {
    override fun observeDistrict(districtId: String): Flow<District?> =
        districtDao.observeDistrict(districtId).map { entity ->
            entity?.let {
                District(it.id, it.state, it.districtNumber, it.displayName, it.representativeId)
            }
        }

    override suspend fun getDistrict(districtId: String) = runCatching { civicApi.fetchDistrict(districtId) }
    override fun observeMetrics(districtId: String): Flow<List<DistrictMetric>> =
        metricDao.observeMetrics(districtId).map { entities ->
            entities.map { entity ->
                DistrictMetric(
                    id = entity.id,
                    districtId = entity.districtId,
                    category = entity.category,
                    name = entity.name,
                    officialValue = entity.officialValue,
                    residentValue = entity.residentValue,
                    unit = entity.unit,
                    source = MetricSource.valueOf(entity.source),
                    reportedAt = entity.reportedAt,
                    reporterPubKey = entity.reporterPubKey
                )
            }
        }

    override suspend fun refreshMetrics(districtId: String) = runCatching { civicApi.fetchMetrics(districtId) }
}
