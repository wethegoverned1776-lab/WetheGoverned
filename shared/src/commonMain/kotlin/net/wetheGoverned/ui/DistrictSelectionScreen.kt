package net.wetheGoverned.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.DistrictRegistry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictSelectionScreen(
    onDistrictSelected: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredDistricts = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            DistrictRegistry.allDistricts
        } else {
            DistrictRegistry.allDistricts.filter { 
                it.displayName.contains(searchQuery, ignoreCase = true) ||
                it.state.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Federal & District Selector") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search State or District") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredDistricts) { district ->
                    ListItem(
                        headlineContent = { Text(district.displayName) },
                        supportingContent = { Text(district.id) },
                        modifier = Modifier.clickable {
                            onDistrictSelected(district.id, district.displayName)
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
