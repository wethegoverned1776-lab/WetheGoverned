package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.model.DistrictPoll
import net.wetheGoverned.model.PollScope
import net.wetheGoverned.session.SessionManager
import javax.inject.Inject

@HiltViewModel
class PollViewModel @Inject constructor(
    private val repository: PollRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _polls = MutableStateFlow<List<DistrictPoll>>(emptyList())
    val polls: StateFlow<List<DistrictPoll>> = _polls

    init {
        observePolls()
    }

    private fun observePolls() = viewModelScope.launch {
        val districtId = sessionManager.currentSession?.districtId ?: "us-fl-06"
        repository.observeDistrictPolls(districtId).collectLatest {
            _polls.value = it
        }
    }

    fun createPoll(
        question: String,
        options: List<String>,
        scope: PollScope,
        onCreated: (pollId: String) -> Unit
    ) = viewModelScope.launch {
        val session = sessionManager.currentSession
        val districtId = session?.districtId ?: "us-fl-06"
        val localId = if (scope == PollScope.LOCAL) session?.localId else null
        
        val result = repository.createPoll(
            districtId = districtId,
            question = question,
            options = options,
            closesAt = null,
            scope = scope,
            localId = localId
        )
        result.onSuccess {
            onCreated(it.id)
        }
    }
}
