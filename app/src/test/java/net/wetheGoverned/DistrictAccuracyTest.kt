package net.wetheGoverned

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import net.wetheGoverned.data.CivicApiImpl
import net.wetheGoverned.model.District
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import kotlinx.coroutines.runBlocking

class DistrictAccuracyTest {

    private val api = CivicApiImpl(
        congressApiKey = "stub",
        openStatesApiKey = "stub",
        googleCivicApiKey = "stub",
        relayUrls = emptyList(),
        httpClient = HttpClient(OkHttp)
    )

    private val testCases = mapOf(
        // Redistricted / Special Interest
        "36104" to "us-al-02", // Montgomery, AL
        "30040" to "us-ga-06", // Cumming, GA
        "70802" to "us-la-06", // Baton Rouge, LA
        "28086" to "us-nc-14", // Kings Mountain, NC
        "13202" to "us-ny-22", // Syracuse, NY
        "32246" to "us-fl-04", // Jacksonville, FL (User's specific case)
        "32137" to "us-fl-06", // Palm Coast, FL (User's latest case)
        
        // Major Cities
        "98101" to "us-wa-07", // Seattle
        "33101" to "us-fl-27", // Miami
        "60601" to "us-il-07", // Chicago
        "10001" to "us-ny-10", // Manhattan
        "90210" to "us-ca-36", // Beverly Hills
        "30301" to "us-ga-05", // Atlanta
        "73301" to "us-tx-10", // Austin
        "19101" to "us-pa-03", // Philadelphia
        "43201" to "us-oh-03", // Columbus
        
        // State Fallbacks (Simulation should return District 1 or similar if state is found)
        "California" to "us-ca-01",
        "Texas" to "us-tx-02",
        "New York" to "us-ny-10",
        "Florida" to "us-fl-06"
    )

    @Test
    fun `test address simulation accuracy`() = runBlocking {
        println("=== District Accuracy Test (Simulation Mode) ===")
        var passed = 0
        var total = 0

        for ((input, expectedId) in testCases) {
            total++
            val result = api.getDistrictFromAddress(input)
            if (result != null && result.id == expectedId) {
                println("✅ PASS: Input '$input' -> ${result.displayName} (${result.id})")
                passed++
            } else {
                println("❌ FAIL: Input '$input' -> Expected $expectedId, but got ${result?.id ?: "null"}")
            }
        }

        println("\nResults: $passed / $total passed.")
        assertEquals("Accuracy should be 100% for defined test cases", total, passed)
    }
}
