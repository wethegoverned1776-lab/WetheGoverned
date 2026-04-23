package net.wetheGoverned.ui.poll

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.wetheGoverned.data.repository.SimplePollRepository

@Composable
fun CreatePollScreen(
    onBack: () -> Unit,
    onCreate: (String) -> Unit   // returns the created pollId
) {
    var question by remember { mutableStateOf("") }
    var optionList by remember { mutableStateOf(mutableListOf("", "")) }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Create Poll") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("Poll Question") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("Options:")
            optionList.forEachIndexed { index, text ->
                OutlinedTextField(
                    value = text,
                    onValueChange = { newText ->
                        optionList = optionList.toMutableList().apply { this[index] = newText }
                    },
                    label = { Text("Option ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            Button(onClick = { optionList.add("") }, modifier = Modifier.fillMaxWidth()) {
                Text("Add Another Option")
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val validOptions = optionList.filter { it.isNotBlank() }
                    if (question.isNotBlank() && validOptions.isNotEmpty()) {
                        // Use a coroutine scope to call the suspend function if necessary,
                        // or call it from the ViewModel. For now, assuming SimplePollRepository is accessible.
                        // Since this is a UI file, calling a suspend function directly in onClick requires a scope.
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Poll")
            }
        }
    }
}
