package net.wetheGoverned.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.wetheGoverned.core.*
import net.wetheGoverned.local.dao.*
import net.wetheGoverned.local.entity.*
import net.wetheGoverned.model.*
import net.wetheGoverned.remote.api.CivicApi
import java.util.UUID
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
        runCatching {
            val manifesto = CandidateManifesto(
                id = UUID.randomUUID().toString(),
                candidatePubKey = candidatePubKey,
                districtId = districtId,
                title = title,
                body = body,
                publishedAt = System.currentTimeMillis(),
                questions = emptyList()
            )

            manifestoDao.upsertManifesto(
                CandidateManifestoEntity(
                    id = manifesto.id,
                    candidatePubKey = manifesto.candidatePubKey,
                    districtId = manifesto.districtId,
                    title = manifesto.title,
                    body = manifesto.body,
                    publishedAt = manifesto.publishedAt,
                    cachedAt = System.currentTimeMillis()
                )
            )
            manifesto
        }

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
                    firstName = it.firstName,
                    lastName = it.lastName,
                    districtId = it.districtId,
                    tier = VerificationTier.valueOf(it.tier),
                    avatarUrl = it.avatarUrl,
                    bio = it.bio,
                    joinedAt = it.joinedAt,
                    addressFingerprint = it.addressFingerprint,
                    verifiedByPubKey = it.verifiedByPubKey
                )
            }
        }

    override fun observeProfileByFingerprint(fingerprint: String): Flow<ResidentProfile?> =
        profileDao.observeProfilesByFingerprint(fingerprint).map { entities ->
            entities.firstOrNull()?.let {
                ResidentProfile(
                    pubKey = it.pubKey,
                    displayName = it.displayName,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    districtId = it.districtId,
                    tier = VerificationTier.valueOf(it.tier),
                    joinedAt = it.joinedAt,
                    addressFingerprint = it.addressFingerprint,
                    verifiedByPubKey = it.verifiedByPubKey
                )
            }
        }

    override suspend fun getResidentCountAtAddress(fingerprint: String): Int {
        return profileDao.getProfileCountByFingerprint(fingerprint)
    }

    override suspend fun getVouchCount(notaryPubKey: String): Int {
        return profileDao.getVouchCount(notaryPubKey)
    }

    suspend fun getAllResidents(): List<ResidentProfile> = 
        profileDao.getAllProfiles().map { entity ->
            ResidentProfile(
                pubKey = entity.pubKey,
                displayName = entity.displayName,
                firstName = entity.firstName,
                lastName = entity.lastName,
                districtId = entity.districtId,
                tier = VerificationTier.valueOf(entity.tier),
                joinedAt = entity.joinedAt,
                addressFingerprint = entity.addressFingerprint,
                verifiedByPubKey = entity.verifiedByPubKey
            )
        }

    override suspend fun getProfile(pubKey: String) = runCatching { civicApi.fetchProfile(pubKey) }
    
    override suspend fun upgradeTier(pubKey: String, newTier: VerificationTier, proofToken: String) =
        runCatching { 
            profileDao.updateTier(pubKey, newTier.name)
            val profile = civicApi.fetchProfile(pubKey).copy(tier = newTier)
            val entity = profileDao.getProfile(pubKey)
            if (entity != null) {
                profileDao.upsertProfile(entity.copy(tier = newTier.name))
            }
            profile
        }

    override suspend fun upgradeTierWithFingerprint(pubKey: String, newTier: VerificationTier, proofToken: String, fingerprint: String) =
        runCatching {
            profileDao.updateTierWithFingerprint(pubKey, newTier.name, fingerprint)
            val profile = civicApi.fetchProfile(pubKey).copy(tier = newTier, addressFingerprint = fingerprint)
            profile
        }

    override suspend fun upgradeTierFull(
        pubKey: String,
        newTier: VerificationTier,
        firstName: String,
        lastName: String,
        fingerprint: String,
        verifiedBy: String?
    ): Result<Unit> = runCatching {
        val existing = profileDao.getProfile(pubKey) ?: throw Exception("Profile not found")
        profileDao.upsertProfile(existing.copy(
            tier = newTier.name,
            firstName = firstName,
            lastName = lastName,
            addressFingerprint = fingerprint,
            verifiedByPubKey = verifiedBy,
            cachedAt = System.currentTimeMillis()
        ))
    }

    override suspend fun updateProfile(pubKey: String, displayName: String, bio: String?, avatarUrl: String?) =
        runCatching { 
            val existing = profileDao.getProfile(pubKey)
            if (existing != null) {
                profileDao.upsertProfile(existing.copy(displayName = displayName, bio = bio, avatarUrl = avatarUrl))
            }
            civicApi.fetchProfile(pubKey).copy(displayName = displayName, bio = bio, avatarUrl = avatarUrl)
        }
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
