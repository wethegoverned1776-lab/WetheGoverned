package net.wetheGoverned.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.wetheGoverned.core.Secp256k1KeyManager
import net.wetheGoverned.model.ResidentProfile
import net.wetheGoverned.model.UserAccount
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.repository.AccountRepository
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.session.SessionManager

class NetworkRegistrationViewModel(
    private val accountRepository: AccountRepository,
    private val residentRepository: ResidentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(NetworkRegUiState())
    val uiState: StateFlow<NetworkRegUiState> = _uiState

    fun register(
        displayName: String,
        address: String,
        stateAbbr: String,
        districtNum: String
    ) {
        val verifierPubKey = sessionManager.currentPubKey ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Generate Username: StateAbbr + DistrictNum + Random 4-digit ID (e.g. FL060001)
            // Ensuring District is 2 digits for consistency
            val cleanDist = districtNum.filter { it.isDigit() }.padStart(2, '0')
            val randomId = (1000..9999).random().toString()
            val generatedUsername = "${stateAbbr.uppercase()}$cleanDist$randomId"
            
            // Generate Temporary Password
            val generatedPassword = "temp_" + (100000..999999).random().toString()
            
            val keyPair = Secp256k1KeyManager.generateKeyPair()
            val account = UserAccount(
                username = generatedUsername, 
                password = generatedPassword, 
                pubKey = keyPair.pubKeyHex, 
                privateKey = keyPair.privateKeyHex, 
                districtId = null,
                requiresPasswordChange = true
            )
            
            val accResult = accountRepository.register(account)
            if (accResult.isSuccess) {
                val profile = ResidentProfile(
                    pubKey = keyPair.pubKeyHex,
                    displayName = displayName,
                    districtId = "us-${stateAbbr.lowercase()}-$cleanDist",
                    tier = VerificationTier.VERIFIED,
                    joinedAt = System.currentTimeMillis(),
                    verifiedByPubKey = verifierPubKey,
                    address = address,
                    isVerified = true
                )
                
                residentRepository.upgradeTierFull(profile.pubKey, profile.tier, "", verifierPubKey)
                residentRepository.updateProfile(profile.pubKey, profile.displayName, null)
                residentRepository.updateDistrict(profile.pubKey, profile.districtId ?: "")
                residentRepository.createProfile(profile)
                
                _uiState.update { it.copy(
                    isLoading = false, 
                    success = true, 
                    generatedUsername = generatedUsername,
                    generatedPassword = generatedPassword
                ) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = accResult.exceptionOrNull()?.message) }
            }
        }
    }

    fun reset() = _uiState.update { NetworkRegUiState() }
}

data class NetworkRegUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val generatedUsername: String? = null,
    val generatedPassword: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkRegistrationScreen(
    viewModel: NetworkRegistrationViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var displayName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var stateAbbr by remember { mutableStateOf("") }
    var districtNum by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invite New Resident") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.success) {
                Icon(Icons.Default.CheckCircle, null, tint = Color.Green, modifier = Modifier.size(64.dp))
                Text("User Registered Successfully", style = MaterialTheme.typography.headlineSmall)
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Credentials to Share:", fontWeight = FontWeight.Bold)
                        Text("Username: ${uiState.generatedUsername}", style = MaterialTheme.typography.bodyLarge)
                        Text("Temp Password: ${uiState.generatedPassword}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                
                Text("The user will be prompted to change this password on their first login.", 
                    style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                
                Button(onClick = onBack) { Text("Return to Profile") }
            } else {
                Text("Enter identity details. The username will be generated based on geography.", 
                    style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                
                OutlinedTextField(value = displayName, onValueChange = { displayName = it }, label = { Text("Legal Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full Address") }, modifier = Modifier.fillMaxWidth())
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = stateAbbr, onValueChange = { stateAbbr = it.take(2) }, label = { Text("State (e.g. FL)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = districtNum, onValueChange = { districtNum = it }, label = { Text("District #") }, modifier = Modifier.weight(1f))
                }

                if (uiState.error != null) {
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = { viewModel.register(displayName, address, stateAbbr, districtNum) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading && displayName.isNotBlank() && stateAbbr.length == 2
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(Modifier.size(24.dp))
                    else Text("Generate Identity & Verify")
                }
            }
        }
    }
}
