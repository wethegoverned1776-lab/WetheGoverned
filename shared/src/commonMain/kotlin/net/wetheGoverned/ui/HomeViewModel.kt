package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.CivicPoll
import net.wetheGoverned.model.PollScope
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager

data class HomeUiState(
    val federalId: String = "us",
    val stateId: String? = null,
    val districtId: String? = null,
    val stateUpperId: String? = null,
    val stateLowerId: String? = null,
    val localId: String? = null,
    val districtDisplayName: String = "No District Assigned",
    val username: String? = null,
    val polls: List<CivicPoll> = emptyList(),
    val filteredPolls: List<CivicPoll> = emptyList(),
    val groupedPolls: Map<String, List<CivicPoll>> = emptyMap(),
    val searchQuery: String = "",
    val selectedScope: PollScope = PollScope.DISTRICT,
    val isOtherDistrict: Boolean = false,
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false, // UX: National Scale Feedback
    val error: String? = null,
)

open class HomeViewModel(
    private val pollRepository: PollRepository,
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager,
    private val relayManager: net.wetheGoverned.data.NostrRelayManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var pollsJob: Job? = null

    init {
        refreshSession()
        observeSyncStatus()
    }

    private fun observeSyncStatus() {
        relayManager.relayStatuses
            .onEach { statuses ->
                val syncing = statuses.values.any { it == net.wetheGoverned.data.RelayStatus.CONNECTING }
                _uiState.update { it.copy(isSyncing = syncing) }
            }
            .launchIn(viewModelScope)
    }

    fun refreshSession() {
        val session = sessionManager.currentSession
        val districtId = session?.districtId
        val stateId = districtId?.substringBeforeLast('-') 
        val isGuest = session?.pubKey == "guest_observer_hex"

        _uiState.update { 
            it.copy(
                username = sessionManager.currentPubKey,
                federalId = "us",
                stateId = stateId,
                districtId = districtId,
                stateUpperId = session?.stateUpperId,
                stateLowerId = session?.stateLowerId,
                localId = session?.localId,
                districtDisplayName = if (districtId == null) (if (isGuest) "Observer Mode" else "No District Assigned") else "District $districtId",
                isOtherDistrict = isGuest
            )
        }
        observePolls()
    }

    private fun observePolls() {
        pollsJob?.cancel()
        val state = _uiState.value
        val hierarchyIds = if (state.districtId == null) {
            listOf("us")
        } else {
            listOfNotNull("us", state.stateId, state.districtId, state.stateUpperId, state.stateLowerId, state.localId)
        }

        pollsJob = pollRepository
            .observePollsByIds(hierarchyIds)
            .map { allPolls ->
                allPolls.sortedWith(compareByDescending<CivicPoll> { it.importanceScore }.thenByDescending { it.createdAt })
            }
            .onEach { polls ->
                _uiState.update { it.copy(polls = polls, isLoading = false, error = null) }
                filterAndGroupPolls()
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterAndGroupPolls()
    }

    private fun filterAndGroupPolls() {
        val state = _uiState.value
        val currentScope = state.selectedScope
        val searchFiltered = if (state.searchQuery.isBlank()) {
            state.polls
        } else {
            state.polls.filter { 
                it.question.contains(state.searchQuery, ignoreCase = true) ||
                it.districtId.contains(state.searchQuery, ignoreCase = true)
            }
        }

        val scopeFiltered = searchFiltered.filter { poll ->
            when (currentScope) {
                PollScope.FEDERAL -> true
                PollScope.STATE -> poll.scope != PollScope.FEDERAL
                PollScope.DISTRICT -> poll.scope == PollScope.DISTRICT || poll.scope == PollScope.LOCAL
                PollScope.LOCAL -> poll.scope == PollScope.LOCAL
            }
        }

        val grouped = scopeFiltered.groupBy { poll ->
            when {
                poll.scope == PollScope.FEDERAL -> "Federal Governance"
                poll.scope == PollScope.STATE && poll.districtId == state.stateId -> "State of ${state.stateId}"
                poll.scope == PollScope.STATE && poll.districtId == state.stateUpperId -> "State Senate"
                poll.scope == PollScope.STATE && poll.districtId == state.stateLowerId -> "State House"
                poll.scope == PollScope.DISTRICT -> "Federal District ${state.districtId ?: "Unknown"}"
                poll.scope == PollScope.LOCAL -> "Local: ${state.localId ?: "Municipality"}"
                else -> "${poll.scope} Governance"
            }
        }

        _uiState.update { it.copy(filteredPolls = scopeFiltered, groupedPolls = grouped) }
    }

    fun setScope(scope: PollScope) {
        if (_uiState.value.districtId == null) return
        _uiState.update { it.copy(selectedScope = scope, isLoading = true) }
        observePolls()
    }

    fun selectDistrict(id: String, displayName: String) {
        val homeDistrict = sessionManager.currentSession?.districtId
        val isOther = homeDistrict != null && homeDistrict != id
        val stateId = id.substringBeforeLast('-')
        
        _uiState.update { 
            it.copy(
                districtId = id, 
                stateId = stateId,
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
        val pubKey = sessionManager.currentPubKey ?: return
        if (_uiState.value.isOtherDistrict || _uiState.value.districtId == null || pubKey.startsWith("guest_")) return

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
