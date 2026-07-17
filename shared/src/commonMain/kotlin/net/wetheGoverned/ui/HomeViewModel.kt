package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager

data class ElectedOfficial(
    val office: String,
    val name: String,
    val party: String,
    val nextElection: String,
    val districtId: String? = null,
    val photoUrl: String? = null
)

data class HomeUiState(
    val federalId: String = "us",
    val stateId: String? = null,
    val federalHouseId: String? = null,
    val stateSenateId: String? = null,
    val stateHouseId: String? = null,
    val countyId: String? = null,
    val cityId: String? = null,
    val schoolBoardId: String? = null,
    val federalHouseName: String = "US House District 6",
    val stateSenateName: String = "State Senate District 7",
    val stateHouseName: String = "State House District 19",
    val countyName: String = "Flagler County",
    val cityName: String = "Palm Coast",
    val schoolBoardName: String = "Flagler School Board",
    val localId: String? = null,
    val districtDisplayName: String = "No District Assigned",
    val username: String? = null,
    val polls: List<CivicPoll> = emptyList(),
    val filteredPolls: List<CivicPoll> = emptyList(),
    val groupedPolls: Map<String, List<CivicPoll>> = emptyMap(),
    val electedOfficials: List<ElectedOfficial> = emptyList(),
    val searchQuery: String = "",
    val selectedScope: PollScope = PollScope.DASHBOARD,
    val isOtherDistrict: Boolean = false,
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val verificationTier: VerificationTier = VerificationTier.OBSERVER,
    val districtsActive: Int = 0,
    val pollsVoted: Int = 0,
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
        val fedHouseId = session?.districtId
        val stateId = fedHouseId?.substringBeforeLast('-', "us") 
        val isGuest = session?.pubKey == "guest_observer_hex"

        val mockOfficials = if (fedHouseId != null) {
            listOf(
                ElectedOfficial("U.S. House (FL-06)", "Randy Fine", "Republican", "Nov 2024", fedHouseId),
                ElectedOfficial("State Senate (Dist 7)", "Tom Leek", "Republican", "Nov 2024", session.stateUpperId),
                ElectedOfficial("State House (Dist 19)", "Sam Greco", "Republican", "Nov 2024", session.stateLowerId),
                ElectedOfficial("County Commissioner", "John Doe", "Non-Partisan", "Nov 2026", session.localId),
                ElectedOfficial("City Council", "Jane Smith", "Non-Partisan", "Nov 2025", session.cityId),
                ElectedOfficial("School Board", "District Z Rep", "Non-Partisan", "Nov 2024", session.schoolBoardId)
            )
        } else emptyList()

        _uiState.update { 
            it.copy(
                username = sessionManager.currentPubKey,
                federalId = "us",
                stateId = stateId,
                federalHouseId = fedHouseId,
                stateSenateId = session?.stateUpperId,
                stateHouseId = session?.stateLowerId,
                countyId = session?.localId,
                localId = session?.localId,
                cityId = session?.cityId,
                schoolBoardId = session?.schoolBoardId,
                districtDisplayName = if (fedHouseId == null) "Select District" else "District $fedHouseId",
                isOtherDistrict = isGuest || session?.tier == VerificationTier.OBSERVER,
                verificationTier = session?.tier ?: VerificationTier.OBSERVER,
                electedOfficials = mockOfficials,
                districtsActive = if (fedHouseId != null) 6 else 0,
                pollsVoted = 12
            )
        }
        observePolls()
    }

    private fun observePolls() {
        pollsJob?.cancel()
        val state = _uiState.value
        val hierarchyIds = if (state.federalHouseId == null) {
            listOf("us")
        } else {
            listOfNotNull("us", state.stateId, state.federalHouseId, state.stateSenateId, state.stateHouseId, state.countyId, state.cityId, state.schoolBoardId)
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

        if (currentScope == PollScope.REPRESENTATIVES) {
            _uiState.update { it.copy(filteredPolls = emptyList(), groupedPolls = emptyMap()) }
            return
        }

        val scopeFiltered = when (currentScope) {
            PollScope.DASHBOARD -> searchFiltered
            PollScope.FEDERAL -> searchFiltered.filter { 
                it.scope == PollScope.FEDERAL || it.districtId == state.federalHouseId 
            }
            PollScope.STATE -> searchFiltered.filter { 
                it.scope == PollScope.STATE || it.districtId == state.stateSenateId || it.districtId == state.stateHouseId
            }
            PollScope.DISTRICT -> searchFiltered.filter { it.scope == PollScope.DISTRICT }
            PollScope.LOCAL -> searchFiltered.filter { 
                it.scope == PollScope.LOCAL || it.districtId == state.countyId || it.districtId == state.cityId || it.districtId == state.schoolBoardId
            }
            PollScope.ALL_POLLS -> searchFiltered
            PollScope.RESULTS -> searchFiltered.filter { it.status == PollStatus.CLOSED }
            else -> searchFiltered
        }

        val hierarchy = linkedMapOf(
            "Federal Governance" to mutableListOf<CivicPoll>(),
            "State Governance" to mutableListOf<CivicPoll>(),
            state.stateSenateName to mutableListOf<CivicPoll>(),
            state.stateHouseName to mutableListOf<CivicPoll>(),
            state.countyName to mutableListOf<CivicPoll>(),
            state.cityName to mutableListOf<CivicPoll>(),
            state.schoolBoardName to mutableListOf<CivicPoll>(),
            "Other Districts" to mutableListOf<CivicPoll>()
        )

        scopeFiltered.forEach { poll ->
            val key = when {
                poll.scope == PollScope.FEDERAL || poll.districtId == state.federalHouseId -> "Federal Governance"
                poll.scope == PollScope.STATE && poll.districtId == state.stateId -> "State Governance"
                poll.districtId == state.stateSenateId -> state.stateSenateName
                poll.districtId == state.stateHouseId -> state.stateHouseName
                poll.districtId == state.countyId -> state.countyName
                poll.districtId == state.cityId -> state.cityName
                poll.districtId == state.schoolBoardId -> state.schoolBoardName
                else -> "Other Districts"
            }
            hierarchy.getOrPut(key) { mutableListOf() }.add(poll)
        }

        val finalGrouped = hierarchy.filter { it.value.isNotEmpty() }
        _uiState.update { it.copy(filteredPolls = scopeFiltered, groupedPolls = finalGrouped) }
    }

    fun setScope(scope: PollScope) {
        if (_uiState.value.federalHouseId == null && scope != PollScope.FEDERAL && scope != PollScope.ALL_POLLS) return
        _uiState.update { it.copy(selectedScope = scope, isLoading = true) }
        observePolls()
    }

    fun selectDistrict(id: String, displayName: String) {
        val homeDistrict = sessionManager.currentSession?.districtId
        val isOther = homeDistrict != null && homeDistrict != id
        val stateId = id.substringBeforeLast('-', "us")
        
        _uiState.update { 
            it.copy(
                federalHouseId = id, 
                stateId = stateId,
                districtDisplayName = displayName, 
                isOtherDistrict = isOther,
                selectedScope = if (isOther) PollScope.LOCAL else it.selectedScope,
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
        if (_uiState.value.isOtherDistrict || _uiState.value.federalHouseId == null || pubKey.startsWith("guest_")) return

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
