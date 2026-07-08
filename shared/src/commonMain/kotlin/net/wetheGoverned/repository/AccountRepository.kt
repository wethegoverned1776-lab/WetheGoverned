package net.wetheGoverned.repository

import net.wetheGoverned.model.UserAccount

/**
 * Shared AccountRepository interface.
 * Manages user accounts and authentication logic.
 * Implementations handle persistence (Room on Android, Preferences on Desktop).
 */
interface AccountRepository {
    suspend fun register(account: UserAccount): Result<Unit>
    suspend fun login(username: String, password: String): Result<UserAccount>
    suspend fun updateDistrict(username: String, districtId: String)
    suspend fun changePassword(username: String, newPassword: String): Result<Unit>
}

/**
 * In-memory implementation for testing or simple sessions.
 */
class InMemoryAccountRepository : AccountRepository {
    private val accounts = mutableListOf(
        UserAccount(
            username = "admin",
            password = "1January012@",
            pubKey = "pub_admin",
            privateKey = "priv_admin",
            districtId = null,
        )
    )
    
    override suspend fun register(account: UserAccount): Result<Unit> {
        val cleanUsername = account.username.trim()
        if (accounts.any { it.username.equals(cleanUsername, ignoreCase = true) }) {
            return Result.failure(Exception("Username already exists"))
        }
        accounts.add(account.copy(username = cleanUsername))
        return Result.success(Unit)
    }

    override suspend fun login(username: String, password: String): Result<UserAccount> {
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()
        
        val account = accounts.find { 
            it.username.equals(cleanUsername, ignoreCase = true) 
        }

        return if (account == null || account.password != cleanPassword) {
            Result.failure(Exception("Invalid username or password"))
        } else {
            Result.success(account)
        }
    }

    override suspend fun updateDistrict(username: String, districtId: String) {
        val index = accounts.indexOfFirst { it.username.equals(username, ignoreCase = true) }
        if (index != -1) {
            accounts[index] = accounts[index].copy(districtId = districtId)
        }
    }

    override suspend fun changePassword(username: String, newPassword: String): Result<Unit> {
        val index = accounts.indexOfFirst { it.username.equals(username, ignoreCase = true) }
        return if (index != -1) {
            accounts[index] = accounts[index].copy(password = newPassword)
            Result.success(Unit)
        } else {
            Result.failure(Exception("User not found"))
        }
    }
}
