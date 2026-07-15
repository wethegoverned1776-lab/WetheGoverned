package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.DistrictMetric
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.repository.DistrictRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager

data class MetricsUiState(
    val groupedMetrics: Map<String, List<DistrictMetric>> = emptyMap(),
    val isLoading: Boolean = true,
    val canReport: Boolean = false,
    val error: String? = null,
)

open class MetricsViewModel(
    private val districtRepository: DistrictRepository,
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetricsUiState())
    val uiState: StateFlow<MetricsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val districtId = sessionManager.currentSession?.districtId ?: "us-fl-06"
        observeMetrics(districtId)
        checkReportPermission()
        viewModelScope.launch { districtRepository.refreshMetrics(districtId) }
    }

    private fun observeMetrics(districtId: String) {
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
            val pubKey = sessionManager.currentPubKey ?: return@launch
            residentRepository.getProfile(pubKey)
                .onSuccess { profile ->
                    _uiState.update { it.copy(canReport = profile.tier == VerificationTier.VERIFIED) }
                }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}
