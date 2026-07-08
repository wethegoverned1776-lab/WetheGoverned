package net.wetheGoverned.data.repository

import net.wetheGoverned.model.UserAccount
import net.wetheGoverned.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAccountRepository @Inject constructor() : AccountRepository {
    override suspend fun register(account: UserAccount): Result<Unit> = Result.success(Unit)
    override suspend fun login(username: String, password: String): Result<UserAccount> = 
        Result.success(UserAccount("admin", "1January012@", "pub", "priv", "us-fl-06"))
    override suspend fun changePassword(username: String, newPassword: String): Result<Unit> = Result.success(Unit)
    override suspend fun updateDistrict(username: String, districtId: String) {}
}
