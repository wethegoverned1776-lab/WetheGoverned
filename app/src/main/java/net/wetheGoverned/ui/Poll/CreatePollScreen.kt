package net.wetheGoverned.ui.poll

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePollScreen(
    onBack: () -> Unit,
    onCreate: (pollId: String) -> Unit,
    viewModel: PollViewModel = hiltViewModel()
) {
    var question by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("", "")) } // min 2 options

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Poll") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Poll question") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text("Options", style = MaterialTheme.typography.titleSmall)
            }

            itemsIndexed(options) { index, option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = option,
                        onValueChange = { new ->
                            options = options.toMutableList().also { it[index] = new }
                        },
                        label = { Text("Option ${index + 1}") },
                        modifier = Modifier.weight(1f)
                    )
                    if (options.size > 2) {
                        IconButton(onClick = {
                            options = options.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove option")
                        }
                    }
                }
            }

            item {
                TextButton(
                    onClick = { options = options + "" },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Add option")
                }
            }

            item {
                val isValid = question.isNotBlank() && options.all { it.isNotBlank() }
                Button(
                    onClick = {
                        viewModel.createPoll(
                            question = question,
                            options = options,
                            onCreated = onCreate
                        )
                    },
                    enabled = isValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Poll")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}