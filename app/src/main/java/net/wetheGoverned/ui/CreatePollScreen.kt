package net.wetheGoverned.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.wetheGoverned.model.PollScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePollScreen(
    onBack: () -> Unit,
    onCreate: (String) -> Unit,   // returns the created pollId
    viewModel: PollViewModel = hiltViewModel()
) {
    var question by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("", "")) }
    
    // Track which scope we are creating for
    var selectedScope by remember { mutableStateOf(PollScope.DISTRICT) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Poll") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                Text("Target Scope:", style = MaterialTheme.typography.titleSmall)
                Row {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedScope == PollScope.DISTRICT,
                            onClick = { selectedScope = PollScope.DISTRICT }
                        )
                        Text("District")
                    }
                    Spacer(Modifier.width(16.dp))
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedScope == PollScope.LOCAL,
                            onClick = { selectedScope = PollScope.LOCAL }
                        )
                        Text("Local")
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
                    onClick = { 
                        options = options + ""
                    }, 
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
                                onCreated = { pollId ->
                                    onCreate(pollId)
                                }
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
