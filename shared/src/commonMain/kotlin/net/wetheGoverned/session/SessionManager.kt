package net.wetheGoverned.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.wetheGoverned.model.VerificationTier

sealed class SessionEvent {
    data class IdentityVerified(val proofToken: String) : SessionEvent()
}

data class UserSession(
    val pubKey: String,
    val displayName: String,
    val districtId: String?,
    val stateUpperId: String? = null,
    val stateLowerId: String? = null,
    val localId: String? = null,
    val tier: VerificationTier = VerificationTier.UNVERIFIED,
    val privateKey: String? = null
)

interface SessionStorage {
    fun saveSession(session: UserSession)
    fun getSession(): UserSession?
    fun clearSession()
    // Secure Key Storage Recommendations
    fun savePrivateKeySecurely(key: String)
    fun getPrivateKeySecurely(): String?
}

class SessionManager(private val storage: SessionStorage? = null) {
    var currentPubKey: String? = null
    var currentSession: UserSession? = null
        private set

    private val _events = MutableSharedFlow<SessionEvent>()
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    init {
        currentSession = storage?.getSession()
        currentPubKey = currentSession?.pubKey
    }

    fun login(
        pubKeyHex: String,
        privateKeyHex: String? = null,
        districtId: String?,
        stateUpperId: String? = null,
        stateLowerId: String? = null,
        localId: String? = null,
        tier: VerificationTier,
        displayName: String
    ) {
        val session = UserSession(pubKeyHex, displayName, districtId, stateUpperId, stateLowerId, localId, tier, privateKeyHex)
        currentPubKey = pubKeyHex
        currentSession = session
        storage?.saveSession(session)
    }

    fun logout() {
        currentPubKey = null
        currentSession = null
        storage?.clearSession()
    }

    fun setDistrict(districtId: String) {
        currentSession = currentSession?.copy(districtId = districtId)?.also {
            storage?.saveSession(it)
        }
    }

    fun setJurisdictions(federalId: String, upperId: String?, lowerId: String?, localId: String?) {
        currentSession = currentSession?.copy(
            districtId = federalId,
            stateUpperId = upperId,
            stateLowerId = lowerId,
            localId = localId
        )?.also {
            storage?.saveSession(it)
        }
    }

    fun upgradeTier(newTier: VerificationTier) {
        currentSession = currentSession?.copy(tier = newTier)?.also {
            storage?.saveSession(it)
        }
    }

    suspend fun emitEvent(event: SessionEvent) {
        _events.emit(event)
    }
}
