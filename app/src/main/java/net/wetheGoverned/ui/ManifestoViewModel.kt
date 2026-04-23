package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.*
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.repository.ManifestoRepository
import net.wetheGoverned.session.SessionManager
import javax.inject.Inject

data class ManifestoUiState(
    val manifestos: List<CandidateManifesto> = emptyList(),
    val districtManifestos: List<CandidateManifesto> = emptyList(),
    val localManifestos: List<CandidateManifesto> = emptyList(),
    val selectedScope: PollScope = PollScope.DISTRICT,
    val isLoading: Boolean = true,
    val userPollActivity: List<UserPollActivity> = emptyList(),
    val error: String? = null,
)

data class UserPollActivity(
    val pollQuestion: String,
    val votedOption: String?,
    val discussionSnippet: String?
)

@HiltViewModel
class ManifestoViewModel @Inject constructor(
    private val manifestoRepository: ManifestoRepository,
    private val pollRepository: PollRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManifestoUiState())
    val uiState: StateFlow<ManifestoUiState> = _uiState.asStateFlow()

    init {
        val districtId = sessionManager.currentSession?.districtId ?: "us-fl-06"
        
        manifestoRepository
            .observeManifestos(districtId)
            .onEach { manifestos ->
                _uiState.update { state -> 
                    state.copy(
                        manifestos = manifestos,
                        districtManifestos = manifestos.filter { it.body.contains("#district", ignoreCase = true) || !it.body.contains("#local", ignoreCase = true) },
                        localManifestos = manifestos.filter { it.body.contains("#local", ignoreCase = true) },
                        isLoading = false
                    ) 
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
            
        loadUserPollActivity()
    }

    private fun loadUserPollActivity() {
        viewModelScope.launch {
            val districtId = sessionManager.currentSession?.districtId ?: "us-fl-06"
            val username = "admin" // Hardwired for now as session doesn't store displayName
            
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

    fun setScope(scope: PollScope) {
        _uiState.update { it.copy(selectedScope = scope) }
    }

    fun publishManifesto(title: String, name: String, body: String, scope: PollScope) {
        viewModelScope.launch {
            val districtId = sessionManager.currentSession?.districtId ?: "us-fl-06"
            val pubKey = sessionManager.currentPubKey ?: "anonymous"
            
            val activityText = if (_uiState.value.userPollActivity.isEmpty()) {
                "No previous poll activity recorded."
            } else {
                _uiState.value.userPollActivity.joinToString("\n") { 
                    "• Poll: ${it.pollQuestion}\n  Vote: ${it.votedOption ?: "None"}\n  Comment: ${it.discussionSnippet ?: "None"}"
                }
            }
            
            val fullBody = "$body\n\n--- Civic Track Record ---\n$activityText"
            val bodyWithTag = if (scope == PollScope.LOCAL) "$fullBody #local" else "$fullBody #district"
            val fullTitle = "$title: $name"

            manifestoRepository.publishManifesto(
                districtId = districtId,
                title = fullTitle,
                body = bodyWithTag,
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
