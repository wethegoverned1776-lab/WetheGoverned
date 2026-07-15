package net.wetheGoverned.data.repository

import net.wetheGoverned.local.dao.AccountDao
import net.wetheGoverned.local.entity.AccountEntity
import net.wetheGoverned.model.UserAccount
import net.wetheGoverned.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAccountRepository @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {
    override suspend fun register(account: UserAccount): Result<Unit> {
        if (accountDao.getAccount(account.username) != null) {
            return Result.failure(Exception("Username already exists"))
        }
        accountDao.upsertAccount(AccountEntity(
            username = account.username,
            password = account.password,
            pubKey = account.pubKey,
            privateKey = account.privateKey,
            districtId = account.districtId,
            requiresPasswordChange = account.requiresPasswordChange
        ))
        return Result.success(Unit)
    }

    override suspend fun login(username: String, password: String): Result<UserAccount> {
        // Seed Admin if it's the first time
        if (username == "admin" && password == "1January012@") {
            val existing = accountDao.getAccount("admin")
            if (existing == null) {
                val admin = AccountEntity("admin", "1January012@", "pub_admin", "priv_admin", "us-fl-06")
                accountDao.upsertAccount(admin)
                return Result.success(UserAccount(admin.username, admin.password, admin.pubKey, admin.privateKey, admin.districtId))
            }
        }

        val account = accountDao.getAccount(username)
        return if (account != null && account.password == password) {
            Result.success(UserAccount(
                username = account.username,
                password = account.password,
                pubKey = account.pubKey,
                privateKey = account.privateKey,
                districtId = account.districtId,
                requiresPasswordChange = account.requiresPasswordChange
            ))
        } else {
            Result.failure(Exception("Invalid username or password"))
        }
    }

    override suspend fun changePassword(username: String, newPassword: String): Result<Unit> {
        val account = accountDao.getAccount(username) ?: return Result.failure(Exception("User not found"))
        accountDao.upsertAccount(account.copy(password = newPassword, requiresPasswordChange = false))
        return Result.success(Unit)
    }

    override suspend fun updateDistrict(username: String, districtId: String) {
        val account = accountDao.getAccount(username) ?: return
        accountDao.upsertAccount(account.copy(districtId = districtId))
    }
}
