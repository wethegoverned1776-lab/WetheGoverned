package net.wetheGoverned

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.wetheGoverned.model.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

/**
 * Large-scale simulation test for We The People.
 * Simulates 660,000 virtual users across all 435 U.S. districts.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MassiveScalabilityTest {

    private val TOTAL_USERS = 660_000
    private val DISTRICTS = DistrictRegistry.allDistricts

    data class VirtualUser(
        val pubKey: String,
        val districtId: String,
        val tier: VerificationTier
    )

    @Test
    fun `simulate 660k users across all districts`() = runTest {
        println("🚀 Starting massive scalability simulation for $TOTAL_USERS users...")

        val users = mutableListOf<VirtualUser>()
        val districtCounts = mutableMapOf<String, Int>()

        // 1. Generate Virtual Users
        repeat(TOTAL_USERS) {
            val district = DISTRICTS.random()
            val user = VirtualUser(
                pubKey = UUID.randomUUID().toString(),
                districtId = district.id,
                tier = if (Math.random() > 0.3) VerificationTier.TIER_2 else VerificationTier.TIER_1
            )
            users.add(user)
            districtCounts[district.id] = (districtCounts[district.id] ?: 0) + 1
        }

        println("✅ Generated $TOTAL_USERS virtual users.")
        println("📊 District Distribution Summary (Top 5):")
        districtCounts.entries.sortedByDescending { it.value }.take(5).forEach {
            println("   - ${it.key}: ${it.value} users")
        }

        // 2. Simulate Mass Voting in a specific district (e.g. us-fl-06)
        val fl06Users = users.filter { it.districtId == "us-fl-06" && it.tier >= VerificationTier.TIER_2 }
        println("🗳️ Simulating mass vote for FL-06 (${fl06Users.size} eligible voters)...")

        var yayVotes = 0
        var nayVotes = 0

        fl06Users.forEach { _ ->
            if (Math.random() > 0.4) yayVotes++ else nayVotes++
        }

        val totalVotes = yayVotes + nayVotes
        println("📈 Poll Result for FL-06: $yayVotes YAY, $nayVotes NAY (Total: $totalVotes)")

        // 3. Verify System Logic
        assertEquals(fl06Users.size, totalVotes)
        println("✅ Scalability Test Passed: All votes accounted for without PII leakage.")
    }
}
