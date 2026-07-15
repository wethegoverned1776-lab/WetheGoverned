package net.wetheGoverned.model

import kotlinx.serialization.Serializable

@Serializable
data class ResidentProfile(
    val pubKey: String,
    val displayName: String,
    val federalHouseId: String? = null,
    val federalSenateId: String? = null,
    val stateSenateId: String? = null,
    val stateHouseId: String? = null,
    val countyId: String? = null,
    val cityId: String? = null,
    val schoolBoardId: String? = null,
    val tier: VerificationTier,
    val avatarUrl: String? = null,
    val joinedAt: Long,
    val addressFingerprint: String? = null,
    val verifiedByPubKey: String? = null,
    val address: String? = null,
    val isVerified: Boolean = false,
    
    // Legacy support (redirects to federalHouseId)
    val districtId: String? = federalHouseId,
    val localId: String? = countyId
)
