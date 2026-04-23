package net.wetheGoverned.model

data class UserAccount(
    val username: String,
    val password: String,
    val displayName: String,
    val districtId: String? = null // Registration starts with no district
)
