package net.wetheGoverned.ui.district

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.DistrictMetric
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.repository.DistrictRepository
import net.wetheGoverned.repository.ResidentRepository
import javax.inject.Inject

data class MetricsUiState(
    val groupedMetrics: Map<String, List<DistrictMetric>> = emptyMap(),
    val isLoading: Boolean = true,
    val canReport: Boolean = false,   // true for Tier 3 residents
    val error: String? = null,
)

@HiltViewModel
class MetricsViewModel @Inject constructor(
    private val districtRepository: DistrictRepository,
    private val residentRepository: ResidentRepository,
) : ViewModel() {

    private val districtId = "us-fl-06"

    private val _uiState = MutableStateFlow(MetricsUiState())
    val uiState: StateFlow<MetricsUiState> = _uiState.asStateFlow()

    init {
        observeMetrics()
        checkReportPermission()
        viewModelScope.launch { districtRepository.refreshMetrics(districtId) }
    }

    private fun observeMetrics() {
        districtRepository
            .observeMetrics(districtId)
            .onEach { metrics ->
                val grouped = metrics.groupBy { it.category }
                _uiState.update { it.copy(groupedMetrics = grouped, isLoading = false) }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
    }

    private fun checkReportPermission() {
        viewModelScope.launch {
            val pubKey = currentUserPubKey() ?: return@launch
            residentRepository.getProfile(pubKey)
                .onSuccess { profile ->
                    _uiState.update { it.copy(canReport = profile.tier == VerificationTier.TIER_3) }
                }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    private fun currentUserPubKey(): String? = null // TODO: inject session
}
