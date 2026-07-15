package net.wetheGoverned.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import net.wetheGoverned.local.dao.ResidentProfileDao
import net.wetheGoverned.local.entity.ResidentProfileEntity
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResidentRepositoryImpl @Inject constructor(
    private val profileDao: ResidentProfileDao
) : ResidentRepository {
    override fun observeProfile(pubKey: String): Flow<ResidentProfile?> = 
        profileDao.observeProfile(pubKey).map { it?.toDomain() }

    override fun observeProfileByFingerprint(fingerprint: String): Flow<ResidentProfile?> = 
        profileDao.observeProfilesByFingerprint(fingerprint).map { it.firstOrNull()?.toDomain() }

    override suspend fun getResidentCountAtAddress(fingerprint: String): Int = 
        profileDao.getProfileCountByFingerprint(fingerprint)

    override suspend fun getVouchCount(notaryPubKey: String): Int = 
        profileDao.getVouchCount(notaryPubKey)

    override suspend fun getProfile(pubKey: String): Result<ResidentProfile> {
        // Seed Admin Profile
        if (pubKey == "pub_admin") {
            val existing = profileDao.getProfile("pub_admin")
            if (existing == null) {
                val admin = ResidentProfileEntity(
                    pubKey = "pub_admin",
                    displayName = "Administrator",
                    federalHouseId = "us-fl-06",
                    federalSenateId = null,
                    stateSenateId = null,
                    stateHouseId = null,
                    countyId = null,
                    cityId = null,
                    schoolBoardId = null,
                    tier = VerificationTier.VERIFIED.name,
                    avatarUrl = null,
                    joinedAt = System.currentTimeMillis(),
                    addressFingerprint = "admin_fp",
                    cachedAt = System.currentTimeMillis()
                )
                profileDao.upsertProfile(admin)
                return Result.success(admin.toDomain())
            }
        }

        val profile = profileDao.getProfile(pubKey)
        return if (profile != null) Result.success(profile.toDomain())
        else Result.failure(Exception("Profile not found"))
    }

    override suspend fun upgradeTier(pubKey: String, newTier: VerificationTier, proofToken: String): Result<ResidentProfile> {
        profileDao.updateTier(pubKey, newTier.name)
        return getProfile(pubKey)
    }

    override suspend fun upgradeTierWithFingerprint(pubKey: String, newTier: VerificationTier, proofToken: String, fingerprint: String): Result<ResidentProfile> {
        profileDao.updateTierWithFingerprint(pubKey, newTier.name, fingerprint)
        return getProfile(pubKey)
    }

    override suspend fun upgradeTierFull(pubKey: String, newTier: VerificationTier, fingerprint: String, verifiedBy: String?): Result<Unit> {
        val existing = profileDao.getProfile(pubKey) ?: return Result.failure(Exception("Profile not found"))
        profileDao.upsertProfile(existing.copy(tier = newTier.name, addressFingerprint = fingerprint, verifiedByPubKey = verifiedBy))
        return Result.success(Unit)
    }

    override suspend fun updateProfile(pubKey: String, displayName: String, avatarUrl: String?): Result<ResidentProfile> {
        val existing = profileDao.getProfile(pubKey) ?: return Result.failure(Exception("Profile not found"))
        val updated = existing.copy(displayName = displayName, avatarUrl = avatarUrl)
        profileDao.upsertProfile(updated)
        return Result.success(updated.toDomain())
    }

    override suspend fun updateDistrict(pubKey: String, districtId: String): Result<Unit> {
        val existing = profileDao.getProfile(pubKey) ?: return Result.failure(Exception("Profile not found"))
        profileDao.upsertProfile(existing.copy(federalHouseId = districtId))
        return Result.success(Unit)
    }

    override fun observeProfilesVerifiedBy(verifierPubKey: String): Flow<List<ResidentProfile>> = 
        // Note: No DAO method for this yet, so we'll just filter all profiles for now or add a query.
        // Actually, ResidentProfileDao has observeProfilesVerifiedBy? No, it has observeProfilesByFingerprint.
        // I should add a query to ResidentProfileDao.
        profileDao.observeProfilesVerifiedBy(verifierPubKey).map { list -> list.map { it.toDomain() } }

    override suspend fun createProfile(profile: ResidentProfile) {
        profileDao.upsertProfile(profile.toEntity())
    }
}

private fun ResidentProfileEntity.toDomain() = ResidentProfile(
    pubKey = pubKey,
    displayName = displayName,
    federalHouseId = federalHouseId,
    federalSenateId = federalSenateId,
    stateSenateId = stateSenateId,
    stateHouseId = stateHouseId,
    countyId = countyId,
    cityId = cityId,
    schoolBoardId = schoolBoardId,
    tier = try { VerificationTier.valueOf(tier) } catch(e: Exception) { VerificationTier.OBSERVER },
    avatarUrl = avatarUrl,
    joinedAt = joinedAt,
    addressFingerprint = addressFingerprint,
    verifiedByPubKey = verifiedByPubKey,
    isVerified = tier == VerificationTier.VERIFIED.name
)

private fun ResidentProfile.toEntity() = ResidentProfileEntity(
    pubKey = pubKey,
    displayName = displayName,
    federalHouseId = federalHouseId,
    federalSenateId = federalSenateId,
    stateSenateId = stateSenateId,
    stateHouseId = stateHouseId,
    countyId = countyId,
    cityId = cityId,
    schoolBoardId = schoolBoardId,
    tier = tier.name,
    avatarUrl = avatarUrl,
    joinedAt = joinedAt,
    addressFingerprint = addressFingerprint,
    verifiedByPubKey = verifiedByPubKey,
    cachedAt = System.currentTimeMillis()
)

@Singleton
class ManifestoRepositoryImpl @Inject constructor() : ManifestoRepository {
    override fun observeManifestos(districtId: String): Flow<List<CandidateManifesto>> = flowOf(emptyList())
    override suspend fun getManifesto(manifestoId: String): Result<CandidateManifesto> = Result.failure(Exception("Stub"))
    override suspend fun publishManifesto(districtId: String, title: String, body: String, candidatePubKey: String): Result<CandidateManifesto> = Result.failure(Exception("Stub"))
    override suspend fun askQuestion(manifestoId: String, questionText: String, askerPubKey: String): Result<ManifestoQuestion> = Result.failure(Exception("Stub"))
    override suspend fun answerQuestion(manifestoId: String, questionId: String, answerText: String, candidatePubKey: String): Result<ManifestoQuestion> = Result.failure(Exception("Stub"))
}

@Singleton
class ScorecardRepositoryImpl @Inject constructor() : ScorecardRepository {
    override fun observeScorecard(districtId: String): Flow<RepresentativeScorecard?> = flowOf(null)
    override suspend fun getScorecard(districtId: String): Result<RepresentativeScorecard> = Result.failure(Exception("Stub"))
    override suspend fun submitMetricReport(districtId: String, category: String, name: String, value: String, unit: String, reporterPubKey: String): Result<DistrictMetric> = Result.failure(Exception("Stub"))
}

@Singleton
class DistrictRepositoryImpl @Inject constructor() : DistrictRepository {
    override fun observeDistrict(districtId: String): Flow<District?> = flowOf(null)
    override suspend fun getDistrict(districtId: String): Result<District> = Result.failure(Exception("Stub"))
    override fun observeMetrics(districtId: String): Flow<List<DistrictMetric>> = flowOf(emptyList())
    override suspend fun refreshMetrics(districtId: String): Result<List<DistrictMetric>> = Result.success(emptyList())
}

@Singleton
class CommunityRepositoryImpl @Inject constructor() : CommunityRepository {
    override fun observePosts(districtId: String, kind: CommunityPostKind?): Flow<List<CommunityPost>> = flowOf(emptyList())
    override suspend fun getPost(postId: String): Result<CommunityPost> = Result.failure(Exception("Stub"))
    override suspend fun createPost(districtId: String, authorPubKey: String, kind: CommunityPostKind, title: String, description: String, price: Double?, location: String?, contactInfo: String?): Result<CommunityPost> = Result.failure(Exception("Stub"))
    override suspend fun deletePost(postId: String): Result<Unit> = Result.success(Unit)
    override suspend fun getAllPosts(): List<CommunityPost> = emptyList()
    override suspend fun syncPost(post: CommunityPost) {}
}

@Singleton
class VerificationRequestRepositoryImpl @Inject constructor(
    private val requestDao: net.wetheGoverned.local.dao.VerificationRequestDao
) : VerificationRequestRepository {
    override fun observeRequestsForDistrict(districtId: String): Flow<List<VerificationRequest>> =
        requestDao.observeRequestsForDistrict(districtId).map { list -> list.map { it.toDomain() } }

    override fun observeRequestsForState(stateId: String): Flow<List<VerificationRequest>> =
        requestDao.observeRequestsForState(stateId).map { list -> list.map { it.toDomain() } }

    override suspend fun createRequest(request: VerificationRequest): Result<Unit> {
        requestDao.upsertRequest(request.toEntity())
        return Result.success(Unit)
    }

    override suspend fun updateRequestStatus(requestId: String, status: VerificationRequestStatus, handledBy: String): Result<Unit> {
        requestDao.updateStatus(requestId, status.name, handledBy)
        return Result.success(Unit)
    }

    override suspend fun getRequest(requestId: String): Result<VerificationRequest> {
        val entity = requestDao.getRequest(requestId) ?: return Result.failure(Exception("Request not found"))
        return Result.success(entity.toDomain())
    }
}

private fun net.wetheGoverned.local.entity.VerificationRequestEntity.toDomain() = VerificationRequest(
    id = id,
    requesterPubKey = requesterPubKey,
    requesterDisplayName = requesterDisplayName,
    email = email,
    districtId = districtId,
    stateId = stateId,
    address = address,
    createdAt = createdAt,
    status = VerificationRequestStatus.valueOf(status),
    handledByPubKey = handledByPubKey
)

private fun VerificationRequest.toEntity() = net.wetheGoverned.local.entity.VerificationRequestEntity(
    id = id,
    requesterPubKey = requesterPubKey,
    requesterDisplayName = requesterDisplayName,
    email = email,
    districtId = districtId,
    stateId = stateId,
    address = address,
    createdAt = createdAt,
    status = status.name,
    handledByPubKey = handledByPubKey,
    cachedAt = System.currentTimeMillis()
)

@Singleton
class VoteRepositoryImpl @Inject constructor() : VoteRepository {
    override fun observeAllVotes(): Flow<List<CivicVote>> = flowOf(emptyList())
    override fun observeVotesByUser(pubKey: String): Flow<List<CivicVote>> = flowOf(emptyList())
    override suspend fun flagVote(voteId: String, reason: String, expiresAt: Long): Result<Unit> = Result.success(Unit)
    override suspend fun disputeVote(voteId: String, comment: String): Result<Unit> = Result.success(Unit)
    override suspend fun resolveVote(voteId: String): Result<Unit> = Result.success(Unit)
    override suspend fun syncVote(vote: CivicVote) {}
}
