package net.wetheGoverned.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.wetheGoverned.local.entity.AccountEntity

@Dao
interface AccountDao {
    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getAccount(username: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAccount(account: AccountEntity)

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getAccountCount(): Int
}
