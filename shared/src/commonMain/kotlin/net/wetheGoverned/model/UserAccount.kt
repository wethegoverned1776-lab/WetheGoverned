package net.wetheGoverned.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAccount(
    val username: String,
    val password: String,
    val pubKey: String,
    val privateKey: String,
    val districtId: String? = null,
    // Loop 75 Fix: Post-Quantum Identity Extension
    val quantumPubKey: String? = null,
    val quantumPrivateKey: String? = null
)
