package net.wetheGoverned.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.wetheGoverned.model.*
import net.wetheGoverned.ui.HomeViewModel
import net.wetheGoverned.ui.HomeUiState
import net.wetheGoverned.ui.ElectedOfficial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToPoll: (String) -> Unit,
    onNavigateToDistrictSelection: () -> Unit,
    onNavigateToManifestos: () -> Unit,
    onNavigateToMetrics: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToCommunityHub: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onCreatePoll: () -> Unit,
    onLogout: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        onNavigateToPoll = onNavigateToPoll,
        onNavigateToDistrictSelection = onNavigateToDistrictSelection,
        onNavigateToManifestos = onNavigateToManifestos,
        onNavigateToMetrics = onNavigateToMetrics,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToCommunityHub = onNavigateToCommunityHub,
        onNavigateToVerification = onNavigateToVerification,
        onCreatePoll = onCreatePoll,
        onLogout = {
            viewModel.logout()
            onLogout()
        },
        onReturnToHomeDistrict = { viewModel.returnToHomeDistrict() },
        onScopeSelected = { viewModel.setScope(it) },
        onImportanceVote = { id, delta -> viewModel.onImportanceVote(id, delta) },
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onNavigateToPoll: (String) -> Unit,
    onNavigateToDistrictSelection: () -> Unit,
    onNavigateToManifestos: () -> Unit,
    onNavigateToMetrics: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToCommunityHub: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onCreatePoll: () -> Unit,
    onLogout: () -> Unit,
    onReturnToHomeDistrict: () -> Unit,
    onScopeSelected: (PollScope) -> Unit,
    onImportanceVote: (String, Int) -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    val isReadOnly = uiState.isOtherDistrict
    val isObserver = uiState.verificationTier == VerificationTier.OBSERVER
    val districtId = uiState.federalHouseId

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.clickable(enabled = isObserver || isReadOnly) {
                            onNavigateToDistrictSelection()
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isReadOnly && !isObserver) "${uiState.districtDisplayName} (READ-ONLY)" else uiState.districtDisplayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            if (isObserver || isReadOnly) {
                                Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(20.dp))
                            }
                        }
                        val localId = uiState.localId
                        if (localId != null && !isReadOnly) {
                            Text(
                                text = "Local: $localId",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        if (uiState.isSyncing) {
                            Text(
                                "Live Sync Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    if (isReadOnly && !isObserver) {
                        TextButton(onClick = onReturnToHomeDistrict) {
                            Text("Back to Home")
                        }
                    }
                    IconButton(onClick = {
                        uiState.username?.let { onNavigateToProfile(it) }
                    }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isReadOnly && !isObserver) {
                FloatingActionButton(onClick = onCreatePoll) {
                    Icon(Icons.Default.Add, contentDescription = "Create Poll")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isObserver && districtId == null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Welcome to WeTheGoverned", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Select a district to browse local and state-level polls.", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onNavigateToDistrictSelection) {
                            Text("Browse Districts")
                        }
                    }
                }
            }

            ScrollableTabRow(
                selectedTabIndex = uiState.selectedScope.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                PollScope.entries.forEach { scope ->
                    Tab(
                        selected = uiState.selectedScope == scope,
                        onClick = { onScopeSelected(scope) },
                        text = { Text(scope.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
                if (uiState.verificationTier == VerificationTier.OBSERVER) {
                    Tab(
                        selected = false,
                        onClick = onNavigateToVerification,
                        text = { Text("Get Verified", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search polls or districts...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )

            if (uiState.isLoading && uiState.polls.isEmpty()) {
                LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
                    items(5) { PollSkeleton() }
                }
            } else {
                when (uiState.selectedScope) {
                    PollScope.DASHBOARD -> {
                        DashboardView(
                            uiState = uiState,
                            isReadOnly = isReadOnly,
                            onImportanceVote = onImportanceVote,
                            onNavigateToPoll = onNavigateToPoll,
                            onNavigateToManifestos = onNavigateToManifestos,
                            onNavigateToMetrics = onNavigateToMetrics,
                            onNavigateToCommunityHub = onNavigateToCommunityHub
                        )
                    }
                    PollScope.REPRESENTATIVES -> {
                        RepresentativesView(uiState.electedOfficials)
                    }
                    else -> {
                        if (uiState.groupedPolls.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (isObserver && districtId == null && uiState.selectedScope != PollScope.FEDERAL) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("No district selected.", style = MaterialTheme.typography.bodyMedium)
                                        Button(onClick = onNavigateToDistrictSelection) {
                                            Text("Select a District")
                                        }
                                    }
                                } else {
                                    Text("No active polls in this scope.")
                                }
                            }
                        } else {
                            PollFeed(
                                uiState = uiState,
                                isReadOnly = isReadOnly,
                                onImportanceVote = onImportanceVote,
                                onNavigateToPoll = onNavigateToPoll
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardView(
    uiState: HomeUiState,
    isReadOnly: Boolean,
    onImportanceVote: (String, Int) -> Unit,
    onNavigateToPoll: (String) -> Unit,
    onNavigateToManifestos: () -> Unit,
    onNavigateToMetrics: () -> Unit,
    onNavigateToCommunityHub: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Elected Officials",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatBadge("Active: ${uiState.districtsActive}")
                    StatBadge("Voted: ${uiState.pollsVoted}")
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.electedOfficials.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { official ->
                            ElectedOfficialCard(
                                official = official,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }

        item {
            Text(
                "Active Polls in My Districts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (uiState.polls.isEmpty()) {
            item {
                Text("No active polls.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        } else {
            val federalPolls = uiState.polls.filter { it.scope == PollScope.FEDERAL }
            val statePolls = uiState.polls.filter { it.scope == PollScope.STATE }
            val localPolls = uiState.polls.filter { it.scope == PollScope.LOCAL }

            if (federalPolls.isNotEmpty()) {
                item { ScopeHeader("Federal") }
                items(federalPolls.take(2), key = { (it as CivicPoll).id }) { poll ->
                    PollImportanceCard(poll as CivicPoll, isReadOnly, { onImportanceVote((poll as CivicPoll).id, it) }, { onNavigateToPoll((poll as CivicPoll).id) })
                }
            }
            
            if (statePolls.isNotEmpty()) {
                item { ScopeHeader("State") }
                items(statePolls.take(2), key = { (it as CivicPoll).id }) { poll ->
                    PollImportanceCard(poll as CivicPoll, isReadOnly, { onImportanceVote((poll as CivicPoll).id, it) }, { onNavigateToPoll((poll as CivicPoll).id) })
                }
            }

            if (localPolls.isNotEmpty()) {
                item { ScopeHeader("Local") }
                items(localPolls.take(2), key = { (it as CivicPoll).id }) { poll ->
                    PollImportanceCard(poll as CivicPoll, isReadOnly, { onImportanceVote((poll as CivicPoll).id, it) }, { onNavigateToPoll((poll as CivicPoll).id) })
                }
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Quick Links",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickLinkCard("Manifestos", Icons.Default.Description, onNavigateToManifestos, Modifier.weight(1f))
                QuickLinkCard("Metrics", Icons.Default.Assessment, onNavigateToMetrics, Modifier.weight(1f))
                QuickLinkCard("Community", Icons.Default.Groups, onNavigateToCommunityHub, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun ScopeHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun ElectedOfficialCard(
    official: ElectedOfficial,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(official.office, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Text(official.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(official.party, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text("Next: ${official.nextElection}", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { /* Message */ },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Message", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun QuickLinkCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RepresentativesView(officials: List<ElectedOfficial>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Your Representatives", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Verified members of your government at all levels.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
        }
        items(officials) { official ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(48.dp).background(Color.LightGray, MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(official.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${official.office} (${official.party})", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Email, null)
                    }
                }
            }
        }
    }
}

@Composable
fun PollFeed(
    uiState: HomeUiState,
    isReadOnly: Boolean,
    onImportanceVote: (String, Int) -> Unit,
    onNavigateToPoll: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        uiState.groupedPolls.forEach { (groupName, polls) ->
            item {
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(polls, key = { (it as CivicPoll).id }) { poll ->
                PollImportanceCard(
                    poll = poll as CivicPoll,
                    isReadOnly = isReadOnly,
                    onImportanceVote = { delta -> onImportanceVote((poll as CivicPoll).id, delta) },
                    onClick = { onNavigateToPoll((poll as CivicPoll).id) }
                )
            }
        }
    }
}

@Composable
fun PollSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Box(Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollImportanceCard(
    poll: CivicPoll,
    isReadOnly: Boolean,
    onImportanceVote: (Int) -> Unit,
    onClick: () -> Unit
) {
    val hasVoted = poll.residentVoteOption != null

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (hasVoted) 
                Color(0xFFF7F9F9)
            else 
                Color.White
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(48.dp)
            ) {
                IconButton(
                    onClick = { onImportanceVote(1) },
                    enabled = !isReadOnly
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        null,
                        tint = if (poll.userImportanceVote > 0) Color.Green else Color.Gray
                    )
                }
                Text(poll.importanceScore.toString(), fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { onImportanceVote(-1) },
                    enabled = !isReadOnly
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        null,
                        tint = if (poll.userImportanceVote < 0) Color.Red else Color.Gray
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = poll.question, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    if (hasVoted) {
                        Icon(
                            Icons.Default.CheckCircle, 
                            "Voted", 
                            tint = MaterialTheme.colorScheme.primary, 
                            modifier = Modifier.size(20.dp).padding(start = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${poll.totalVotes} votes", style = MaterialTheme.typography.bodySmall)
                    Surface(
                        color = when(poll.scope) {
                            PollScope.FEDERAL -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)
                            PollScope.STATE -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                            PollScope.DISTRICT -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                            PollScope.LOCAL -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                        },
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = poll.scope.name,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
