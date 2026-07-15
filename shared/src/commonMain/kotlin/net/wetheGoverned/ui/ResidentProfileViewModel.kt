package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.*
import net.wetheGoverned.session.SessionManager

data class ResidentProfileUiState(
    val profile: ResidentProfile? = null,
    val isLoading: Boolean = true,
    val isOwnProfile: Boolean = false,
    val registeredUsers: List<ResidentProfile> = emptyList(),
    val verificationRequests: List<VerificationRequest> = emptyList(),
    val requestSent: Boolean = false,
    val error: String? = null,
)

open class ResidentProfileViewModel(
    private val residentRepository: ResidentRepository,
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
    private val requestRepository: VerificationRequestRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResidentProfileUiState())
    val uiState: StateFlow<ResidentProfileUiState> = _uiState.asStateFlow()

    fun load(pubKey: String) {
        // Observe cache
        residentRepository
            .observeProfile(pubKey)
            .onEach { profile ->
                val isGuest = pubKey == "guest_observer_hex"
                _uiState.update {
                    it.copy(
                        profile = profile ?: if (isGuest) ResidentProfile(
                            pubKey = "guest_observer_hex",
                            displayName = "Observer",
                            districtId = sessionManager.currentSession?.districtId,
                            tier = net.wetheGoverned.model.VerificationTier.OBSERVER,
                            joinedAt = 0
                        ) else null,
                        isLoading = profile == null && !isGuest,
                        isOwnProfile = pubKey == sessionManager.currentPubKey,
                    )
                }
                
                // If it's the user's own profile, fetch users they verified
                if (pubKey == sessionManager.currentPubKey) {
                    loadVerifiedByMe(pubKey)
                    if (profile?.tier == VerificationTier.VERIFIED) {
                        loadVerificationRequests(profile.districtId ?: "", profile.stateHouseId ?: profile.districtId?.substringBeforeLast("-") ?: "")
                    }
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)

        // Fetch from remote
        viewModelScope.launch {
            residentRepository.getProfile(pubKey)
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun loadVerifiedByMe(myPubKey: String) {
        residentRepository
            .observeProfilesVerifiedBy(myPubKey)
            .onEach { users ->
                _uiState.update { it.copy(registeredUsers = users) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadVerificationRequests(districtId: String, stateId: String) {
        requestRepository
            .observeRequestsForState(stateId)
            .onEach { requests ->
                _uiState.update { it.copy(verificationRequests = requests.filter { r -> r.status == VerificationRequestStatus.PENDING }) }
            }
            .launchIn(viewModelScope)
    }

    fun requestVerification(email: String, address: String) {
        val pubKey = sessionManager.currentPubKey ?: return
        val profile = _uiState.value.profile ?: return
        val districtId = profile.districtId ?: "us-fl-06"
        val stateId = districtId.substringBeforeLast("-")

        viewModelScope.launch {
            val request = VerificationRequest(
                id = "req_${pubKey}_${Clock.System.now().toEpochMilliseconds()}",
                requesterPubKey = pubKey,
                requesterDisplayName = profile.displayName,
                email = email,
                districtId = districtId,
                stateId = stateId,
                address = address
            )
            requestRepository.createRequest(request).onSuccess {
                _uiState.update { it.copy(requestSent = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun handleRequest(requestId: String, verify: Boolean) {
        val myPubKey = sessionManager.currentPubKey ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = requestRepository.getRequest(requestId).getOrNull() ?: return@launch
            
            if (verify) {
                residentRepository.upgradeTierFull(
                    pubKey = request.requesterPubKey,
                    newTier = VerificationTier.VERIFIED,
                    fingerprint = "verified_from_request",
                    verifiedBy = myPubKey
                ).onSuccess {
                    requestRepository.updateRequestStatus(requestId, VerificationRequestStatus.VERIFIED, myPubKey)
                }.onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                    return@launch
                }
            } else {
                requestRepository.updateRequestStatus(requestId, VerificationRequestStatus.CLOSED, myPubKey)
            }
            
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun getMyPubKey(): String? = sessionManager.currentPubKey
    fun getMyTier(): net.wetheGoverned.model.VerificationTier = sessionManager.currentSession?.tier ?: net.wetheGoverned.model.VerificationTier.OBSERVER

    fun onUpdateDistrict(districtId: String) {
        val pubKey = sessionManager.currentPubKey ?: return
        val username = sessionManager.currentSession?.displayName ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // 1. Update Resident Identity
            val res1 = residentRepository.updateDistrict(pubKey, districtId)
            
            // 2. Update Account Credentials (Uses username for lookup)
            accountRepository.updateDistrict(username, districtId)
            
            // 3. Update active session
            sessionManager.setDistrict(districtId)

            if (res1.isFailure) {
                _uiState.update { it.copy(isLoading = false, error = res1.exceptionOrNull()?.message) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun verifyResident(otherPubKey: String) {
        val myPubKey = sessionManager.currentPubKey ?: return
        val mySession = sessionManager.currentSession ?: return
        if (mySession.tier != net.wetheGoverned.model.VerificationTier.VERIFIED) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            residentRepository.upgradeTierFull(
                pubKey = otherPubKey,
                newTier = net.wetheGoverned.model.VerificationTier.VERIFIED,
                fingerprint = "verified_by_peer",
                verifiedBy = myPubKey
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                load(otherPubKey) // Refresh
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun logout() {
        sessionManager.logout()
    }
}
