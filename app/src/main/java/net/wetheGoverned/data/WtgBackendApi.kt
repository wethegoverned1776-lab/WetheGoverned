package net.wetheGoverned.remote.backend

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ─────────────────────────────────────────────────────────────────────────────
// WtgBackendApi
//
// HTTP client for the WeTheGoverned backend REST API.
// Replaces the `delay()` stubs in TierVerificationScreens and OnboardingViewModel.
//
// Base URL: https://api.wetheGoverned.net/v1
//
// Endpoints:
//   POST /auth/email          – send magic-link verification email
//   POST /auth/confirm        – confirm magic-link token → Tier 1 proof
//   POST /verify/address      – submit address for district cross-check → Tier 2 proof
//   POST /verify/identity     – create Persona identity session → session URL
//   POST /verify/identity/confirm – confirm identity webhook → Tier 3 proof
// ─────────────────────────────────────────────────────────────────────────────

// ── Request / response shapes ─────────────────────────────────────────────────

@Serializable
data class SendEmailRequest(
    @SerialName("pub_key")    val pubKey: String,
    val email: String,
    @SerialName("district_id") val districtId: String,
)

@Serializable
data class SendEmailResponse(val success: Boolean, val message: String = "")

@Serializable
data class ConfirmEmailRequest(
    @SerialName("pub_key") val pubKey: String,
    val token: String,
)

@Serializable
data class ProofTokenResponse(
    @SerialName("proof_token") val proofToken: String,
    val tier: Int,
)

@Serializable
data class AddressVerifyRequest(
    @SerialName("pub_key")     val pubKey: String,
    val address: String,
    val city: String,
    @SerialName("zip_code")    val zipCode: String,
    @SerialName("district_id") val districtId: String,
)

@Serializable
data class IdentitySessionRequest(
    @SerialName("pub_key")     val pubKey: String,
    @SerialName("district_id") val districtId: String,
)

@Serializable
data class IdentitySessionResponse(
    @SerialName("session_id")  val sessionId: String,
    @SerialName("session_url") val sessionUrl: String,
)

@Serializable
data class IdentityConfirmRequest(
    @SerialName("pub_key")    val pubKey: String,
    @SerialName("session_id") val sessionId: String,
)

// ── API class ─────────────────────────────────────────────────────────────────

class WtgBackendApi(
    private val baseUrl: String = "https://api.wetheGoverned.net/v1",
    private val httpClient: HttpClient = buildHttpClient(),
) {

    // ── Email verification ────────────────────────────────────────────────────

    /**
     * Sends a magic-link to the user's email.
     * The link contains a short-lived JWT token that encodes the pubKey.
     * Deep link: wtg://verify?token=<jwt>&tier=1
     */
    suspend fun sendVerificationEmail(
        pubKey: String,
        email: String,
        districtId: String,
    ): SendEmailResponse = httpClient.post("$baseUrl/auth/email") {
        contentType(ContentType.Application.Json)
        setBody(SendEmailRequest(pubKey, email, districtId))
    }.body()

    /**
     * Called after the user taps the magic-link and the app receives the deep link.
     * Returns a Tier 1 proof token.
     */
    suspend fun confirmEmailToken(pubKey: String, token: String): ProofTokenResponse =
        httpClient.post("$baseUrl/auth/confirm") {
            contentType(ContentType.Application.Json)
            setBody(ConfirmEmailRequest(pubKey, token))
        }.body()

    // ── Address verification (Tier 2) ─────────────────────────────────────────

    /**
     * Cross-checks the submitted address against Google Civic Information API
     * to confirm the resident lives in their declared district.
     * Returns a signed proof token on success.
     */
    suspend fun submitAddressVerification(
        pubKey: String,
        address: String,
        city: String,
        zipCode: String,
        districtId: String,
    ): ProofTokenResponse = httpClient.post("$baseUrl/verify/address") {
        contentType(ContentType.Application.Json)
        setBody(AddressVerifyRequest(pubKey, address, city, zipCode, districtId))
    }.body()

    // ── Identity verification (Tier 3) ────────────────────────────────────────

    /**
     * Creates a Persona identity verification session.
     * Returns a session URL to open in the browser for ID + liveness check.
     */
    suspend fun createIdentitySession(
        pubKey: String,
        districtId: String,
    ): IdentitySessionResponse = httpClient.post("$baseUrl/verify/identity") {
        contentType(ContentType.Application.Json)
        setBody(IdentitySessionRequest(pubKey, districtId))
    }.body()

    /**
     * Confirms identity verification after Persona webhook fires.
     * Called when the app receives wtg://identity-verified?proof=<sessionId>.
     */
    suspend fun confirmIdentitySession(
        pubKey: String,
        sessionId: String,
    ): ProofTokenResponse = httpClient.post("$baseUrl/verify/identity/confirm") {
        contentType(ContentType.Application.Json)
        setBody(IdentityConfirmRequest(pubKey, sessionId))
    }.body()

    // ── HTTP client ───────────────────────────────────────────────────────────

    companion object {
        fun buildHttpClient() = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 20_000
                connectTimeoutMillis = 10_000
            }
            // Retry transient 5xx errors up to 2 times
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 2)
                exponentialDelay()
            }
        }
    }
}
