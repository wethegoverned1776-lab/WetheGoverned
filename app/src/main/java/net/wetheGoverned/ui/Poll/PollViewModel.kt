package net.wetheGoverned.ui.poll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.wetheGoverned.data.repository.PollRepository
import net.wetheGoverned.model.DistrictPoll
import javax.inject.Inject

@HiltViewModel
class PollViewModel @Inject constructor(
    private val repository: PollRepository
) : ViewModel() {

    private val _polls = MutableStateFlow<List<DistrictPoll>>(emptyList())
    val polls: StateFlow<List<DistrictPoll>> = _polls

    init { loadPolls() }

    fun loadPolls() = viewModelScope.launch {
        _polls.value = repository.getPolls()
    }

    fun createPoll(
        question: String,
        options: List<String>,
        onCreated: (pollId: String) -> Unit
    ) = viewModelScope.launch {
        val poll = repository.createPoll(question, options)
        loadPolls()
        onCreated(poll.id)
    }
}