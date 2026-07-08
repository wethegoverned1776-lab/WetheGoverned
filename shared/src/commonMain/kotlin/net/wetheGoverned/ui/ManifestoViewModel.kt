package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.repository.ManifestoRepository
import net.wetheGoverned.session.SessionManager

data class UserPollActivity(
    val pollQuestion: String,
    val votedOption: String?,
    val discussionSnippet: String?
)

data class ManifestoUiState(
    val manifestos: List<CandidateManifesto> = emptyList(),
    val filteredManifestos: List<CandidateManifesto> = emptyList(),
    val selectedScope: CivicScope = CivicScope.DISTRICT,
    val federalId: String = "us",
    val stateId: String? = null,
    val districtId: String? = null,
    val isLoading: Boolean = true,
    val userPollActivity: List<UserPollActivity> = emptyList(),
    val error: String? = null,
)

open class ManifestoViewModel(
    private val manifestoRepository: ManifestoRepository,
    private val pollRepository: PollRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManifestoUiState())
    val uiState: StateFlow<ManifestoUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val session = sessionManager.currentSession
        val districtId = session?.districtId ?: "us-fl-06"
        val stateId = districtId.substringBeforeLast('-')

        _uiState.update { it.copy(districtId = districtId, stateId = stateId, isLoading = true) }
        
        // In a real app, we'd observe across multiple districtIds or the repository would handle the hierarchy.
        // For now, observing the primary district and relying on scope filtering.
        manifestoRepository
            .observeManifestos(districtId)
            .onEach { manifestos ->
                _uiState.update { state -> 
                    state.copy(
                        manifestos = manifestos,
                        isLoading = false
                    ) 
                }
                filterManifestos()
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
            
        loadUserPollActivity()
    }

    private fun filterManifestos() {
        val state = _uiState.value
        val filtered = state.manifestos.filter { it.scope == state.selectedScope }
        _uiState.update { it.copy(filteredManifestos = filtered) }
    }

    private fun loadUserPollActivity() {
        viewModelScope.launch {
            val districtId = _uiState.value.districtId ?: return@launch
            val username = sessionManager.currentSession?.displayName ?: "admin"
            
            val polls = pollRepository.observeDistrictPolls(districtId).firstOrNull() ?: emptyList()
            val activity = polls.mapNotNull { poll ->
                if (poll.residentVoteOption != null) {
                    val votedLabel = poll.options.find { it.id == poll.residentVoteOption }?.label
                    
                    val discussion = pollRepository.observePollPosts(poll.id).firstOrNull()
                        ?.find { it.authorName == username }?.content
                        
                    UserPollActivity(poll.question, votedLabel, discussion)
                } else null
            }
            _uiState.update { it.copy(userPollActivity = activity) }
        }
    }

    fun setScope(scope: CivicScope) {
        _uiState.update { it.copy(selectedScope = scope) }
        filterManifestos()
    }

    fun publishManifesto(title: String, name: String, body: String, scope: CivicScope) {
        viewModelScope.launch {
            val districtId = _uiState.value.districtId ?: return@launch
            val pubKey = sessionManager.currentPubKey ?: "anonymous"
            
            val activityText = if (_uiState.value.userPollActivity.isEmpty()) {
                "No previous poll activity recorded."
            } else {
                _uiState.value.userPollActivity.joinToString("\n") { 
                    "• Poll: ${it.pollQuestion}\n  Vote: ${it.votedOption ?: "None"}\n  Comment: ${it.discussionSnippet ?: "None"}"
                }
            }
            
            val fullBody = "$body\n\n--- Civic Track Record ---\n$activityText"
            val fullTitle = "$title: $name"

            // Note: In real app, the repository should accept the scope.
            // Currently using a simplified version.
            manifestoRepository.publishManifesto(
                districtId = districtId,
                title = fullTitle,
                body = fullBody,
                candidatePubKey = pubKey
            )
        }
    }

    fun loadManifesto(manifestoId: String) {
        viewModelScope.launch {
            manifestoRepository.getManifesto(manifestoId)
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}
