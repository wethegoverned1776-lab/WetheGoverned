package net.wetheGoverned.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton
import net.wetheGoverned.model.VerificationTier

sealed class SessionEvent {
    data class IdentityVerified(val proofToken: String) : SessionEvent()
}

@Singleton
class SessionManager @Inject constructor() {
    var currentPubKey: String? = null
    var currentSession: UserSession? = null

    private val _events = MutableSharedFlow<SessionEvent>()
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    fun login(
        pubKeyHex: String,
        privateKeyHex: String,
        districtId: String?, // Changed to nullable
        localId: String? = null,
        tier: VerificationTier,
        displayName: String
    ) {
        currentPubKey = pubKeyHex
        currentSession = UserSession(pubKeyHex, displayName, districtId, localId, tier)
    }

    fun logout() {
        currentPubKey = null
        currentSession = null
    }

    fun setDistrict(districtId: String) {
        currentSession = currentSession?.copy(districtId = districtId)
    }

    fun upgradeTier(newTier: VerificationTier) {
        currentSession = currentSession?.copy(tier = newTier)
    }

    suspend fun emitEvent(event: SessionEvent) {
        _events.emit(event)
    }
}

data class UserSession(
    val pubKey: String,
    val displayName: String,
    val districtId: String?, // Changed to nullable
    val localId: String? = null,
    val tier: VerificationTier = VerificationTier.UNVERIFIED
)

@Singleton
class PendingEventQueue @Inject constructor() {
    // Stub for pending event management
}
