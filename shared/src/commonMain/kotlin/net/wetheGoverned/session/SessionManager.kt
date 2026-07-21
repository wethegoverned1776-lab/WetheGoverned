package net.wetheGoverned.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.wetheGoverned.model.VerificationTier

sealed class SessionEvent {
    data class IdentityVerified(val proofToken: String) : SessionEvent()
}

data class UserSession(
    val pubKey: String,
    val displayName: String,
    val districtId: String?, // Federal House ID (legacy name kept for compatibility)
    val stateUpperId: String? = null, // State Senate
    val stateLowerId: String? = null, // State House
    val localId: String? = null, // County
    val cityId: String? = null,
    val schoolBoardId: String? = null,
    val tier: VerificationTier = VerificationTier.OBSERVER,
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
    private val _session = MutableStateFlow<UserSession?>(storage?.getSession())
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    var currentPubKey: String? = _session.value?.pubKey
    val currentSession: UserSession? get() = _session.value

    private val _events = MutableSharedFlow<SessionEvent>()
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    fun login(
        pubKeyHex: String,
        privateKeyHex: String? = null,
        districtId: String?,
        stateUpperId: String? = null,
        stateLowerId: String? = null,
        localId: String? = null,
        cityId: String? = null,
        schoolBoardId: String? = null,
        tier: VerificationTier,
        displayName: String
    ) {
        val session = UserSession(
            pubKeyHex, displayName, districtId, stateUpperId, stateLowerId, 
            localId, cityId, schoolBoardId, tier, privateKeyHex
        )
        currentPubKey = pubKeyHex
        _session.value = session
        storage?.saveSession(session)
    }

    fun logout() {
        currentPubKey = null
        _session.value = null
        storage?.clearSession()
    }

    fun setDistrict(districtId: String) {
        val updated = _session.value?.copy(districtId = districtId)
        if (updated != null) {
            _session.value = updated
            storage?.saveSession(updated)
        }
    }

    fun setJurisdictions(federalId: String, upperId: String?, lowerId: String?, localId: String?) {
        val updated = _session.value?.copy(
            districtId = federalId,
            stateUpperId = upperId,
            stateLowerId = lowerId,
            localId = localId
        )
        if (updated != null) {
            _session.value = updated
            storage?.saveSession(updated)
        }
    }

    fun upgradeTier(newTier: VerificationTier) {
        val updated = _session.value?.copy(tier = newTier)
        if (updated != null) {
            _session.value = updated
            storage?.saveSession(updated)
        }
    }

    suspend fun emitEvent(event: SessionEvent) {
        _events.emit(event)
    }
}
