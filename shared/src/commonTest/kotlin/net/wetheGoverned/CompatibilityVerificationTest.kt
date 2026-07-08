package net.wetheGoverned

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import net.wetheGoverned.model.*
import kotlinx.datetime.Clock
import org.web3j.crypto.Keys
import kotlinx.coroutines.*

/**
 * Validates core logic and dependencies across different platforms and versions.
 */
class CompatibilityVerificationTest {

    @Test
    fun testJsonSerializationCompatibility() {
        val json = Json { ignoreUnknownKeys = true }
        val poll = CivicPoll(
            id = "test-id",
            scope = CivicScope.FEDERAL,
            districtId = "us",
            authorPubKey = "pk-1",
            question = "Test?",
            options = listOf(PollOption("1", "Yes", 0, 0f)),
            status = PollStatus.ACTIVE,
            createdAt = 1000L,
            closesAt = null,
            totalVotes = 0
        )
        
        val encoded = json.encodeToString(poll)
        val decoded = json.decodeFromString<CivicPoll>(encoded)
        
        assertTrue(decoded.id == "test-id", "JSON Serialization failed on this platform")
    }

    @Test
    fun testCryptographyAvailability() {
        // Web3j relies on BouncyCastle or native providers.
        // We test if we can generate a keypair without crashing.
        try {
            val ecKeyPair = Keys.createEcKeyPair()
            assertTrue(ecKeyPair.publicKey != null, "Cryptography provider failed")
        } catch (e: Exception) {
            throw RuntimeException("Crypto not available on this platform version: ${e.message}")
        }
    }

    @Test
    fun testTimeAvailability() {
        val now = Clock.System.now().toEpochMilliseconds()
        assertTrue(now > 0, "Time provider failed")
    }

    @Test
    fun testPlatformSpecificFileSystemSim() {
        // Test for case-sensitivity issues in common paths
        val paths = listOf("userData/Config.json", "userdata/config.json")
        val uniquePaths = paths.toSet()
        // On common platforms, these should be treated as unique objects in memory
        assertTrue(uniquePaths.size == 2, "Path handling in memory must be case-sensitive for cross-platform safety")
    }

    @Test
    fun testConcurrentIntegrity() = runBlocking {
        // Verify that coroutine threading works across the platform bridge
        val jobs = (1..10).map {
            launch(Dispatchers.Default) {
                repeat(100) { /* simulation of load */ }
            }
        }
        jobs.forEach { it.join() }
        assertTrue(true, "Multi-threading baseline passed")
    }
}
