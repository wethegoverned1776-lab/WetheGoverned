package net.wetheGoverned.util

interface EmailSender {
    suspend fun sendVerificationCode(email: String, code: String): Result<Unit>
}

expect fun createEmailSender(): EmailSender
