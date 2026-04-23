package net.wetheGoverned.data.repository

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import net.wetheGoverned.model.UserAccount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor() {
    // Hardwired admin account initialized with NO district to force verification flow
    private val _accounts = MutableStateFlow<List<UserAccount>>(listOf(
        UserAccount(
            username = "admin",
            password = "1January012@",
            displayName = "Administrator",
            districtId = null 
        )
    ))
    
    fun register(account: UserAccount): Result<Unit> {
        val cleanUsername = account.username.trim()
        if (_accounts.value.any { it.username.equals(cleanUsername, ignoreCase = true) }) {
            return Result.failure(Exception("Username already exists"))
        }
        
        _accounts.update { it + account.copy(username = cleanUsername) }
        Log.i("AccountRepository", "REGISTER SUCCESS: '${cleanUsername}' created")
        return Result.success(Unit)
    }

    fun login(username: String, password: String): Result<UserAccount> {
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()
        
        Log.d("AccountRepository", "Attempting login for: '${cleanUsername}'")
        
        val account = _accounts.value.find { 
            it.username.equals(cleanUsername, ignoreCase = true) 
        }

        return if (account == null) {
            Log.w("AccountRepository", "LOGIN FAILED: User '${cleanUsername}' not found")
            Result.failure(Exception("Invalid username or password"))
        } else if (account.password != cleanPassword) {
            Log.w("AccountRepository", "LOGIN FAILED: Incorrect password for '${cleanUsername}'.")
            Result.failure(Exception("Invalid username or password"))
        } else {
            Log.i("AccountRepository", "LOGIN SUCCESS: '${cleanUsername}' authenticated")
            Result.success(account)
        }
    }

    fun updateDistrict(username: String, districtId: String) {
        _accounts.update { list ->
            list.map { 
                if (it.username.equals(username, ignoreCase = true)) it.copy(districtId = districtId) else it 
            }
        }
    }
}
