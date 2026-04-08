package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.wetheGoverned.model.CandidateManifesto
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.repository.ManifestoRepository
import net.wetheGoverned.repository.ResidentRepository
import javax.inject.Inject

data class ManifestoUiState(
    val manifestos: List<CandidateManifesto> = emptyList(),
    val isLoading: Boolean = true,
    val canAsk: Boolean = false,
    val pendingQuestion: String = "",
    val error: String? = null,
)

@HiltViewModel
class ManifestoViewModel @Inject constructor(
    private val manifestoRepository: ManifestoRepository,
    private val residentRepository: ResidentRepository,
) : ViewModel() {

    private val districtId = "us-fl-06"

    private val _uiState = MutableStateFlow(ManifestoUiState())
    val uiState: StateFlow<ManifestoUiState> = _uiState.asStateFlow()

    init {
        manifestoRepository
            .observeManifestos(districtId)
            .onEach { manifestos ->
                _uiState.update { it.copy(manifestos = manifestos, isLoading = false) }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
    }

    fun loadManifesto(manifestoId: String) {
        viewModelScope.launch {
            manifestoRepository.getManifesto(manifestoId)
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun onQuestionChange(text: String) = _uiState.update { it.copy(pendingQuestion = text) }

    fun onAskQuestion(manifestoId: String) {
        val text = _uiState.value.pendingQuestion.trim()
        if (text.isBlank()) return
        // Stub implementation
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}
