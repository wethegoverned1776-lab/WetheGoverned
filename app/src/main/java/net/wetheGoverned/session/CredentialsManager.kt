package net.wetheGoverned.session

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.wetheGoverned.model.VerificationTier
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialsManager @Inject constructor(
    @ApplicationContext context: Context
) : SessionStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_USERNAME = "last_username"
        private const val KEY_PASSWORD = "last_password"
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    override fun saveSession(session: UserSession) {
        prefs.edit()
            .putString("pubKey", session.pubKey)
            .putString("displayName", session.displayName)
            .putString("districtId", session.districtId)
            .putString("localId", session.localId)
            .putString("tier", session.tier.name)
            .putString("privateKey", session.privateKey)
            .apply()
    }

    override fun getSession(): UserSession? {
        val pubKey = prefs.getString("pubKey", null) ?: return null
        val tierString = prefs.getString("tier", VerificationTier.OBSERVER.name) ?: VerificationTier.OBSERVER.name
        val tier = try {
            VerificationTier.valueOf(tierString)
        } catch (e: IllegalArgumentException) {
            // Migration for renamed/removed tiers
            when (tierString) {
                "TIER_2", "TIER_3" -> VerificationTier.VERIFIED
                else -> VerificationTier.OBSERVER
            }
        }

        return UserSession(
            pubKey = pubKey,
            displayName = prefs.getString("displayName", "") ?: "",
            districtId = prefs.getString("districtId", null),
            localId = prefs.getString("localId", null),
            tier = tier,
            privateKey = prefs.getString("privateKey", null)
        )
    }

    override fun clearSession() {
        prefs.edit().clear().apply()
    }

    override fun savePrivateKeySecurely(key: String) {
        prefs.edit().putString("secure_nsec", key).apply()
    }

    override fun getPrivateKeySecurely(): String? = prefs.getString("secure_nsec", null)

    fun saveCredentials(username: String, password: String) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_REMEMBER_ME, true)
            .apply()
    }

    fun clearCredentials() {
        prefs.edit().clear().apply()
    }

    fun getSavedUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getSavedPassword(): String? = prefs.getString(KEY_PASSWORD, null)
    fun hasSavedSession(): Boolean = prefs.getBoolean(KEY_REMEMBER_ME, false)
}
