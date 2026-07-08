package net.wetheGoverned.model

import kotlinx.serialization.Serializable

@Serializable
data class ResidentProfile(
    val pubKey: String,
    val displayName: String,
    val districtId: String?,
    val localId: String? = null,
    val tier: VerificationTier,
    val avatarUrl: String? = null,
    val joinedAt: Long,
    val addressFingerprint: String? = null,
    val verifiedByPubKey: String? = null,
    val address: String? = null,
    val isVerified: Boolean = false
)
