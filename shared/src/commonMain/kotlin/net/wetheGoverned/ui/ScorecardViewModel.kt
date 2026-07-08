package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.CivicScope
import net.wetheGoverned.model.RepresentativeScorecard
import net.wetheGoverned.repository.ScorecardRepository
import net.wetheGoverned.session.SessionManager

data class ScorecardUiState(
    val scorecard: RepresentativeScorecard? = null,
    val selectedScope: CivicScope = CivicScope.DISTRICT,
    val federalId: String = "us",
    val stateId: String? = null,
    val districtId: String? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)

open class ScorecardViewModel(
    private val scorecardRepository: ScorecardRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScorecardUiState())
    val uiState: StateFlow<ScorecardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val session = sessionManager.currentSession
        val districtId = session?.districtId ?: "us-fl-06"
        val stateId = districtId.substringBeforeLast('-')

        _uiState.update { it.copy(districtId = districtId, stateId = stateId) }
        
        observeScorecard()
    }

    private fun observeScorecard() {
        val state = _uiState.value
        val fetchId = when(state.selectedScope) {
            CivicScope.FEDERAL -> state.federalId
            CivicScope.STATE -> state.stateId ?: state.districtId ?: "us-fl"
            CivicScope.DISTRICT -> state.districtId ?: "us-fl-06"
            CivicScope.LOCAL -> state.districtId ?: "us-fl-06"
        }

        scorecardRepository
            .observeScorecard(fetchId)
            .onEach { scorecard ->
                _uiState.update { it.copy(scorecard = scorecard, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)

        onRefresh()
    }

    fun setScope(scope: CivicScope) {
        _uiState.update { it.copy(selectedScope = scope, isLoading = true) }
        observeScorecard()
    }

    fun onRefresh() {
        val state = _uiState.value
        val fetchId = when(state.selectedScope) {
            CivicScope.FEDERAL -> state.federalId
            CivicScope.STATE -> state.stateId ?: state.districtId ?: "us-fl"
            CivicScope.DISTRICT -> state.districtId ?: "us-fl-06"
            CivicScope.LOCAL -> state.districtId ?: "us-fl-06"
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            scorecardRepository.getScorecard(fetchId)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onSubmitMetricReport(
        category: String,
        name: String,
        value: String,
        unit: String,
        reporterPubKey: String,
    ) {
        val districtId = _uiState.value.districtId ?: return
        viewModelScope.launch {
            scorecardRepository.submitMetricReport(
                districtId = districtId,
                category = category,
                name = name,
                value = value,
                unit = unit,
                reporterPubKey = reporterPubKey,
            ).onFailure { e ->
                _uiState.update { it.copy(error = "Report failed: ${e.message}") }
            }
        }
    }
}
