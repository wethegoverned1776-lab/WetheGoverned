package net.wetheGoverned.remote.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.wetheGoverned.util.createEmailSender

@Serializable
data class SendEmailRequest(
    @SerialName("pub_key")    val pubKey: String,
    val email: String,
    @SerialName("district_id") val districtId: String,
)

@Serializable
data class SendEmailResponse(
    val success: Boolean, 
    val message: String = "",
    val simCode: String? = null // Used for local simulation/SMTP testing
)

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

open class WtgBackendApi(
    private val baseUrl: String = "https://api.wetheGoverned.net/v1",
    private val httpClient: HttpClient
) {
    private val localSender = createEmailSender()

    open suspend fun sendVerificationEmail(
        pubKey: String,
        email: String,
        districtId: String,
    ): SendEmailResponse {
        // Generate the code locally so we can send it via SMTP if needed
        val chars = "0123456789ABCDEF"
        val code = (1..6).map { chars.random() }.joinToString("")

        // If using a simulation URL, use local SMTP immediately
        if (baseUrl.contains("sim.")) {
            localSender.sendVerificationCode(email, code)
            return SendEmailResponse(success = true, message = "Simulation: Code sent via local SMTP.", simCode = code)
        }

        return try {
            httpClient.post("$baseUrl/auth/email") {
                contentType(ContentType.Application.Json)
                setBody(SendEmailRequest(pubKey, email, districtId))
            }.body()
        } catch (e: Exception) {
            // Attempt fallback via local SMTP (hMailServer)
            val smtpResult = localSender.sendVerificationCode(email, code)
            if (smtpResult.isSuccess) {
                SendEmailResponse(success = true, message = "Code sent via SMTP.", simCode = code)
            } else {
                val errorMsg = smtpResult.exceptionOrNull()?.message ?: "Unknown SMTP error"
                SendEmailResponse(success = false, message = "SMTP Failure: $errorMsg. (Check your mail server settings)")
            }
        }
    }

    open suspend fun confirmEmailToken(pubKey: String, token: String): ProofTokenResponse {
        if (baseUrl.contains("sim.")) {
            return ProofTokenResponse(proofToken = "sim_proof_${pubKey.take(8)}", tier = 1)
        }
        
        return httpClient.post("$baseUrl/auth/confirm") {
            contentType(ContentType.Application.Json)
            setBody(ConfirmEmailRequest(pubKey, token))
        }.body()
    }
}
