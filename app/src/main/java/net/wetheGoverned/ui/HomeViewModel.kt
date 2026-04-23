package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.CivicPoll
import net.wetheGoverned.model.PollScope
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pollRepository: PollRepository,
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var pollsJob: Job? = null

    init {
        refreshSession()
    }

    fun refreshSession() {
        val session = sessionManager.currentSession
        _uiState.update { 
            it.copy(
                username = sessionManager.currentPubKey,
                districtId = session?.districtId,
                localId = session?.localId,
                districtDisplayName = if (session?.districtId == null) "No District Assigned" else "District ${session.districtId}"
            )
        }
        observePolls()
    }

    private fun observePolls() {
        pollsJob?.cancel()
        val districtId = _uiState.value.districtId ?: return
        val localId = _uiState.value.localId
        val scope = _uiState.value.selectedScope

        pollsJob = pollRepository
            .observeDistrictPolls(districtId)
            .map { polls ->
                val filtered = when (scope) {
                    PollScope.DISTRICT -> polls.filter { it.scope == PollScope.DISTRICT }
                    PollScope.LOCAL -> polls.filter { it.scope == PollScope.LOCAL && (localId == null || it.localId == localId) }
                }
                filtered.sortedWith(compareByDescending<CivicPoll> { it.importanceScore }.thenByDescending { it.createdAt })
            }
            .onEach { polls ->
                _uiState.update { it.copy(polls = polls, isLoading = false, error = null) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun setScope(scope: PollScope) {
        if (_uiState.value.isOtherDistrict || _uiState.value.districtId == null) return
        _uiState.update { it.copy(selectedScope = scope, isLoading = true) }
        observePolls()
    }

    fun selectDistrict(id: String, displayName: String) {
        val homeDistrict = sessionManager.currentSession?.districtId
        val isOther = homeDistrict != null && homeDistrict != id
        
        _uiState.update { 
            it.copy(
                districtId = id, 
                districtDisplayName = displayName, 
                isOtherDistrict = isOther,
                selectedScope = if (isOther) PollScope.DISTRICT else it.selectedScope,
                isLoading = true 
            ) 
        }
        observePolls()
    }

    fun returnToHomeDistrict() {
        val homeSession = sessionManager.currentSession ?: return
        val districtId = homeSession.districtId ?: return
        selectDistrict(districtId, "Home District")
    }

    fun onImportanceVote(pollId: String, delta: Int) {
        if (_uiState.value.isOtherDistrict || _uiState.value.districtId == null) return
        val pubKey = sessionManager.currentPubKey ?: return
        viewModelScope.launch {
            pollRepository.voteImportance(pollId, delta, pubKey)
                .onFailure { e ->
                    _uiState.update { it.copy(error = "Importance vote failed: ${e.message}") }
                }
        }
    }

    fun logout() {
        sessionManager.logout()
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}

data class HomeUiState(
    val districtId: String? = null,
    val localId: String? = null,
    val districtDisplayName: String = "No District Assigned",
    val username: String? = null,
    val polls: List<CivicPoll> = emptyList(),
    val selectedScope: PollScope = PollScope.DISTRICT,
    val isOtherDistrict: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)
