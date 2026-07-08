package net.wetheGoverned.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class AccountEntity(
    @PrimaryKey val username: String,
    val password: String,
    val pubKey: String,
    val privateKey: String,
    val districtId: String?,
    val createdAt: Long = System.currentTimeMillis(),
)
