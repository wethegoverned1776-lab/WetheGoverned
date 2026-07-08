package net.wetheGoverned

import net.wetheGoverned.util.MnemonicUtils
import org.junit.Test
import java.security.MessageDigest
import kotlin.math.pow

class SecurityAuditSimulator {

    @Test
    fun auditUserVulnerability() {
        println("--- AUDIT: USER VULNERABILITY ---")
        
        // 1. Mnemonic Entropy Check
        // The current implementation uses a custom word list.
        // Let's count the words (simulated from code review)
        val wordListSize = 250 // Rough count from provided snippet
        val bitEntropy = (Math.log(wordListSize.toDouble()) / Math.log(2.0)) * 12
        println("Mnemonic Entropy: ${bitEntropy.toInt()} bits")
        if (bitEntropy < 128) {
            println("VULNERABILITY: Low Entropy Secret Phrase. Standard is 128+ bits.")
        }

        // 2. Private Key Exposure
        println("Identity Storage: Local SQLite/Room")
        println("VULNERABILITY: Rooted devices or physical access can extract Private Keys.")
    }

    @Test
    fun auditNetworkVulnerability() {
        println("\n--- AUDIT: NETWORK VULNERABILITY ---")
        
        // 1. MITM Attack
        println("Protocol: HTTP (Standard)")
        println("VULNERABILITY: P2P Gossip is currently unencrypted. MITM can sniff votes/profiles.")

        // 2. DoS Vulnerability
        println("Server: Ktor/Netty on Port 80/8080")
        println("VULNERABILITY: No rate-limiting in P2PSyncEngine. Malicious peer can exhaust connections.")
    }

    @Test
    fun auditVoteVulnerability() {
        println("\n--- AUDIT: VOTE VULNERABILITY ---")
        
        // 1. Sybil Attack (Address Reuse)
        val address = "123 Main St"
        val fingerprint1 = hash(address)
        val fingerprint2 = hash(address.uppercase() + " ")
        println("Address 1: $address -> $fingerprint1")
        println("Address 2: ${address.uppercase()}  -> $fingerprint2")
        
        if (fingerprint1 != fingerprint2) {
            println("VULNERABILITY: Case-sensitivity or whitespace in address can bypass 'One Resident, One Vote'.")
        }

        // 2. Replay Attack
        println("Vote Structure: Lacks Nonce/Sequence Number")
        println("VULNERABILITY: Signed votes can be re-broadcasted to overwrite newer choices.")
    }

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
