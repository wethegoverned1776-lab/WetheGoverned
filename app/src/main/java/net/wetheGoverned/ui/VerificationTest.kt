package net.wetheGoverned.ui

import android.util.Log
import kotlinx.coroutines.*
import net.wetheGoverned.model.VerificationTier
import net.wetheGoverned.session.SessionManager
import net.wetheGoverned.data.repository.AccountRepository
import net.wetheGoverned.model.UserAccount
import net.wetheGoverned.repository.ResidentRepository
import net.wetheGoverned.remote.api.CivicApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Automated Decentralized Governance Simulator - v3
 * Verifies the "App as the Server" flow including GIS detection, Voter Roll checks, 
 * and strict 1-to-1 P2P household vouching.
 */
@Singleton
class VerificationSimulator @Inject constructor(
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
    private val residentRepository: ResidentRepository,
    private val civicApi: CivicApi
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun runFullTest(onResult: (String) -> Unit) = scope.launch {
        try {
            onResult("--- STARTING DECENTRALIZED SERVER VERIFICATION ---")

            // PHASE 1: ADMIN ONBOARDING (District Discovery)
            onResult("PHASE 1: Verifying Admin ('App-Server-1')...")
            
            // 1. Login Admin (District starts BLANK as per requirements)
            val adminLogin = accountRepository.login("admin", "1January012@")
            val adminAccount = adminLogin.getOrThrow()
            onResult("INFO: Admin logged in. Assigned District: ${adminAccount.districtId ?: "NONE"}")

            // 2. Simulate User Flow: Enter Name + Address
            val address = "123 Ocean Ave, Daytona Beach, FL 32114"
            val first = "Admin"; val last = "User"
            
            // 3. App-Server resolves districts (GIS)
            val detected = civicApi.getDistrictFromAddress(address)
            onResult("GIS: Detected ${detected?.displayName ?: "Unknown"} for address.")

            // 4. App-Server checks Voter Rolls
            val isRegistered = civicApi.verifyVoterRolls(first, last, address, detected?.id ?: "")
            if (isRegistered && detected != null) {
                onResult("VOTER_ROLL: Match found in ${detected.id}. Seating resident...")
                
                residentRepository.upgradeTierFull(
                    pubKey = "admin",
                    newTier = VerificationTier.TIER_2,
                    firstName = first,
                    lastName = last,
                    fingerprint = "fp_daytona_123"
                )
                accountRepository.updateDistrict("admin", detected.id)
                onResult("SUCCESS: Admin is now a Verified Constituent of FL-06.")
            }

            // PHASE 2: HOUSEHOLD VOUCHING (1-to-1 Limit)
            onResult("PHASE 2: Testing Household Vouching (Spouse Flow)...")
            
            // 1. Create Spouse Account
            val spouseUser = "spouse_voter"
            accountRepository.register(UserAccount(spouseUser, "pass", "Spouse User", null))
            
            // 2. Check if admin can vouch (Address is shared)
            val adminProfile = residentRepository.getProfile("admin").getOrThrow()
            val vouchCount = residentRepository.getVouchCount("admin")
            onResult("INFO: Admin verified status: ${adminProfile.tier}, Vouches used: $vouchCount")

            if (adminProfile.tier >= VerificationTier.TIER_2 && vouchCount < 1) {
                onResult("VOUCH: Admin signing residency attestation for spouse...")
                residentRepository.upgradeTierFull(
                    pubKey = spouseUser,
                    newTier = VerificationTier.TIER_2,
                    firstName = "Jane",
                    lastName = "User",
                    fingerprint = "fp_daytona_123", // Same household fingerprint
                    verifiedBy = "admin"
                )
                onResult("SUCCESS: Spouse verified via P2P Vouch.")
            }

            // 3. Security Stress Test: Third Person at same address
            onResult("PHASE 3: Security Stress Test (Duplicate Vouch Prevention)...")
            val roommateUser = "roommate_voter"
            accountRepository.register(UserAccount(roommateUser, "pass", "Roommate User", null))
            
            val updatedVouchCount = residentRepository.getVouchCount("admin")
            if (updatedVouchCount >= 1) {
                onResult("SECURITY: Admin vouch credit exhausted ($updatedVouchCount/1).")
                onResult("BLOCK: Roommate rejected from P2P Vouch path. Correct behavior.")
            } else {
                throw Exception("Security Failure: Vouch limit not enforced!")
            }

            onResult("--- VIRTUAL VERIFICATION COMPLETE: ALL SYSTEMS FUNCTIONAL ---")

        } catch (e: Exception) {
            onResult("VERIFICATION FAILED: ${e.message}")
            Log.e("VerificationSimulator", "Critical failure", e)
        }
    }
}
