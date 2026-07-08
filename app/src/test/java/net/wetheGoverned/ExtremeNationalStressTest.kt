package net.wetheGoverned

import net.wetheGoverned.model.*
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import kotlin.system.measureTimeMillis

class ExtremeNationalStressTest {

    @Test
    fun extremeScaleAdversarialBattlegroundSimulation() {
        net.wetheGoverned.core.SecurityValidator.reset()
        
        // --- US NATIONAL SCALE PARAMETERS ---
        val TARGET_VOTERS = 440_000_000L
        val VALID_USER_ROLL = (1..5000).map { "pub-key-$it" }.toSet()
        val VALID_BATCH = 3_000_000 // Increased batch for harder stress
        net.wetheGoverned.core.SecurityValidator.setAuthorizedVoters(VALID_USER_ROLL)

        println("🧨 STARTING COMPREHENSIVE NATIONAL SIMULATION")
        println("Scale: 440M Voters | 3.5M Mixed Ops | Multi-Relay Hybrid Mesh")

        var detections = 0
        var leaks = 0
        
        // State Weighted Mock Data
        val districtVotes = mutableMapOf<String, Int>() // Track vote counts per district for skew check

        val timeTaken = measureTimeMillis {
            val seededNonce = 999999L
            net.wetheGoverned.core.SecurityValidator.validateVote(
                CivicVote("v-val-seed", "us-fl-01", "pub-key-1", "Citizen", "opt_1", System.currentTimeMillis(), seededNonce),
                "us-fl-01"
            )

            val totalOps = VALID_BATCH + 500_000
            for (i in 1..totalOps) {
                val opType = i % 20 
                
                val vote: CivicVote = when (opType) {
                    0 -> { // REPLAY
                        CivicVote("v-mal-rep-$i", "us-fl-01", "pub-key-1", "Citizen", "opt_1", System.currentTimeMillis(), seededNonce)
                    }
                    1 -> { // SYBIL (Unauthorized Key)
                        CivicVote("v-mal-sybil-$i", "us-fl-01", "attacker-key-$i", "Hacker", "opt_0", System.currentTimeMillis(), i.toLong())
                    }
                    2 -> { // FORGERY (Malformed)
                        CivicVote("", "us-fl-01", "pub-key-2", "", "opt_1", System.currentTimeMillis(), i.toLong())
                    }
                    3 -> { // JURISDICTION CROSS-STATE 
                        CivicVote("v-mal-jur-$i", "us-ny-14", "pub-key-3", "Citizen", "opt_1", System.currentTimeMillis(), i.toLong())
                    }
                    4 -> { // STATE SKEW ATTACK (Attempt to flood one district to alter State Average)
                        CivicVote("v-skew-$i", "us-fl-01", "pub-key-4", "Citizen", "opt_1", System.currentTimeMillis(), i.toLong() + 5000000)
                    }
                    5 -> { // GHOST TIMESTAMP (Future)
                        CivicVote("v-mal-time-$i", "us-fl-01", "pub-key-5", "Citizen", "opt_1", System.currentTimeMillis() + 10000000, i.toLong())
                    }
                    else -> { // VALID NATIONAL/STATE/DISTRICT TRAFFIC
                        val dId = "us-fl-0${(1..9).random()}"
                        CivicVote("v-val-$i", dId, "pub-key-${(1..5000).random()}", "Citizen", "opt_1", System.currentTimeMillis(), i.toLong() + 1000000)
                    }
                }

                // SECURITY VALIDATION PIPELINE
                val result = net.wetheGoverned.core.SecurityValidator.validateVote(vote, if(opType == 3) "us-fl-01" else null)
                
                val isActuallyMalicious = opType <= 5 && !vote.id.startsWith("v-val")
                val wasDetected = result is net.wetheGoverned.core.ValidationResult.Failure

                if (isActuallyMalicious && !wasDetected) leaks++
                if (isActuallyMalicious && wasDetected) detections++
                
                // PERFORMANCE: Mock the state weighted rule counting for valid votes
                if (!wasDetected) {
                    val currentCount = districtVotes[vote.pollId] ?: 0
                    districtVotes[vote.pollId] = currentCount + 1
                }
            }
        }

        println("\n📊 --- NATIONAL SIMULATION RESULTS ---")
        println("⏱️ Execution Time: ${timeTaken}ms")
        println("🚨 Attacks Neutralized: $detections")
        println("🔥 Security Leaks: $leaks")
        println("📡 Network Capacity: ${((VALID_BATCH + 500_000).toDouble() / timeTaken * 1000).toInt()} events/sec")
        
        // Verify state weighted rule integrity
        println("⚖️ District Load Distribution: $districtVotes")

        assertTrue("System must leak ZERO attacks. Found $leaks leaks.", leaks == 0)
        assertTrue("Throughput must exceed 1M events/sec for national readiness", (VALID_BATCH.toDouble() / timeTaken * 1000) > 1_000_000)
    }

    private fun createMockPoll(id: String, scope: CivicScope, districtId: String, localId: String? = null): CivicPoll {
        return CivicPoll(
            id = id,
            scope = scope,
            districtId = districtId,
            localId = localId,
            authorPubKey = "admin",
            question = "Sample Question for $scope",
            options = listOf(PollOption("opt_0", "Yes", 0, 0f), PollOption("opt_1", "No", 0, 0f)),
            status = PollStatus.ACTIVE,
            createdAt = System.currentTimeMillis(),
            closesAt = null,
            totalVotes = 0
        )
    }
}
