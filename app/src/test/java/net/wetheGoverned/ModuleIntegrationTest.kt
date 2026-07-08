package net.wetheGoverned

import net.wetheGoverned.model.CivicVote
import net.wetheGoverned.util.AddressUtils
import net.wetheGoverned.util.MnemonicUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModuleIntegrationTest {

    @Test
    fun `test identity and vote creation flow across modules`() {
        // 1. Shared Module: Identity Generation
        val mnemonic = MnemonicUtils.generateMnemonic()
        assertTrue("Mnemonic should be valid", MnemonicUtils.validateMnemonic(mnemonic))
        val seed = MnemonicUtils.generateSeed(mnemonic)
        assertNotNull("Seed should be generated from mnemonic", seed)

        // 2. Shared Module: Address Fingerprinting (Sybil Prevention)
        val street = "1600 Pennsylvania Ave NW"
        val city = "Washington"
        val zip = "20500"
        
        val fingerprint = AddressUtils.generateFingerprint(street, city, zip)
        
        // Verify cross-module consistency: Different formatting should yield same result
        val alternateFingerprint = AddressUtils.generateFingerprint("1600 pennsylvania avenue n.w.", "WASHINGTON", "20500-0001")
        assertEquals("Fingerprints must match despite formatting", fingerprint, alternateFingerprint)

        // 3. App Module Integration: Create a Vote model using shared definitions
        val vote = CivicVote(
            id = "vote_${System.currentTimeMillis()}",
            pollId = "poll_123",
            voterPubKey = "0xTestPubKey",
            voterName = "Citizen One",
            optionId = "opt_yes",
            timestamp = System.currentTimeMillis(),
            nonce = System.nanoTime() 
        )

        // Validate the integrated object
        assertEquals("opt_yes", vote.optionId)
        assertTrue("Vote must have a nonce", vote.nonce > 0)
        
        println("Integration Test Passed:")
        println(" - Identity: BIP-39 phrase verified")
        println(" - Security: Address Fingerprint ($fingerprint) is consistent")
        println(" - Integrity: CivicVote model with nonce ${vote.nonce} created")
    }
}
