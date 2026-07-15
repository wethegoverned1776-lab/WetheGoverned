package net.wetheGoverned.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.wetheGoverned.repository.PollRepository
import net.wetheGoverned.model.CivicPoll
import net.wetheGoverned.model.PollScope
import net.wetheGoverned.session.SessionManager

open class PollViewModel(
    private val repository: PollRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _polls = MutableStateFlow<List<CivicPoll>>(emptyList())
    val polls: StateFlow<List<CivicPoll>> = _polls

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
        val currentDistrictId = session?.districtId ?: "us-fl-06"
        
        // Determine the target district hierarchy based on scope
        val targetDistrictId = when (scope) {
            PollScope.FEDERAL -> "us"
            PollScope.STATE -> currentDistrictId.substringBeforeLast('-') // e.g. "us-fl-06" -> "us-fl"
            PollScope.DISTRICT -> currentDistrictId
            PollScope.LOCAL -> currentDistrictId
            else -> currentDistrictId // Fallback for DASHBOARD, ALL_POLLS, REPRESENTATIVES, RESULTS
        }
        
        val localId = if (scope == PollScope.LOCAL) session?.localId else null
        
        val result = repository.createPoll(
            districtId = targetDistrictId,
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
