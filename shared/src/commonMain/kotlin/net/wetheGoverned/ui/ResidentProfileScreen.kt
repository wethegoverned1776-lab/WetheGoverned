package net.wetheGoverned.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Verified
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.VerificationTier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentProfileScreen(
    pubKey: String,
    viewModel: ResidentProfileViewModel,
    onBack: () -> Unit,
    onUpgradeTier: () -> Unit,
    onNavigateToDistrictSelection: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(pubKey) {
        viewModel.load(pubKey)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isOwnProfile) {
                        IconButton(onClick = { /* TODO: Implement Edit */ }) {
                            Icon(Icons.Default.Edit, "Edit Profile")
                        }
                        IconButton(onClick = {
                            viewModel.logout()
                            onLogout()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Logout")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.profile != null) {
            val profile = uiState.profile!!
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(profile.displayName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("District: ${profile.districtId ?: "Not assigned"}", style = MaterialTheme.typography.bodyMedium)
                        if (uiState.isOwnProfile) {
                            Button(
                                onClick = onNavigateToDistrictSelection,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (profile.districtId == null) MaterialTheme.colorScheme.primary 
                                                     else MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(if (profile.districtId == null) "Determine My District" else "Change")
                            }
                        }
                    }

                    Text("Verification: ${profile.tier}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }

                if (uiState.isOwnProfile) {
                    if (profile.isVerified || profile.tier != VerificationTier.UNVERIFIED) {
                        item {
                            Button(
                                onClick = onNavigateToRegistration,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text("Register New User to Network")
                            }
                        }
                    }

                    item {
                        Button(onClick = onUpgradeTier, modifier = Modifier.fillMaxWidth()) {
                            Text("Upgrade Verification Tier")
                        }
                    }

                    if (uiState.registeredUsers.isNotEmpty()) {
                        item {
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            Text(
                                "Members You Invited",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        items(uiState.registeredUsers) { user ->
                            ListItem(
                                headlineContent = { Text(user.displayName, fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("Joined ${net.wetheGoverned.core.formatDate(user.joinedAt)}", style = MaterialTheme.typography.bodySmall) },
                                trailingContent = {
                                    Icon(
                                        Icons.Default.Verified, 
                                        contentDescription = "Verified", 
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                    
                    item {
                        OutlinedButton(
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Sign Out")
                        }
                    }
                }
            }
        } else {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Profile not found.")
            }
        }
    }
}
