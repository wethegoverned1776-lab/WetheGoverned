package net.wetheGoverned.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.PollScope

@Composable
private fun ScopeOptionItem(
    label: String,
    scope: PollScope,
    selectedScope: PollScope,
    onSelect: (PollScope) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        RadioButton(
            selected = selectedScope == scope,
            onClick = { onSelect(scope) }
        )
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePollScreen(
    onBack: () -> Unit,
    onCreate: (String) -> Unit,
    viewModel: PollViewModel
) {
    var question by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("", "")) }
    var selectedScope by remember { mutableStateOf(PollScope.DISTRICT) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Poll") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                Text("Target Scope:", style = MaterialTheme.typography.titleSmall)
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ScopeOptionItem("Federal", PollScope.FEDERAL, selectedScope) { selectedScope = it }
                        ScopeOptionItem("State", PollScope.STATE, selectedScope) { selectedScope = it }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ScopeOptionItem("District", PollScope.DISTRICT, selectedScope) { selectedScope = it }
                        ScopeOptionItem("Local", PollScope.LOCAL, selectedScope) { selectedScope = it }
                    }
                }
                
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Poll Question") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Text("Options:", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
            }

            itemsIndexed(options) { index, text ->
                OutlinedTextField(
                    value = text,
                    onValueChange = { newText ->
                        val newList = options.toMutableList()
                        newList[index] = newText
                        options = newList
                    },
                    label = { Text("Option ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                Button(
                    onClick = { options = options + "" }, 
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Another Option")
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        val validOptions = options.filter { it.isNotBlank() }
                        if (question.isNotBlank() && validOptions.size >= 2) {
                            viewModel.createPoll(
                                question = question,
                                options = validOptions,
                                scope = selectedScope,
                                onCreated = { pollId -> onCreate(pollId) }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = question.isNotBlank() && options.filter { it.isNotBlank() }.size >= 2
                ) {
                    Text("Create Poll")
                }
            }
        }
    }
}
