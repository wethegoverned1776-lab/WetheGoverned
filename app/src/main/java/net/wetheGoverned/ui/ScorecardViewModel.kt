package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.RepresentativeScorecard
import net.wetheGoverned.repository.ScorecardRepository
import javax.inject.Inject

data class ScorecardUiState(
    val scorecard: RepresentativeScorecard? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ScorecardViewModel @Inject constructor(
    private val scorecardRepository: ScorecardRepository,
) : ViewModel() {

    private val districtId = "us-fl-06"   // TODO: inject from session / nav arg

    private val _uiState = MutableStateFlow(ScorecardUiState())
    val uiState: StateFlow<ScorecardUiState> = _uiState.asStateFlow()

    init {
        scorecardRepository
            .observeScorecard(districtId)
            .onEach { scorecard ->
                _uiState.update { it.copy(scorecard = scorecard, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)

        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            scorecardRepository.getScorecard(districtId)
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
