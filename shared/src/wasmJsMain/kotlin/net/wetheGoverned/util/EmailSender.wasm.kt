package net.wetheGoverned.util

actual fun createEmailSender(): EmailSender = object : EmailSender {
    override suspend fun sendVerificationCode(email: String, code: String): Result<Unit> {
        return Result.success(Unit)
    }
}
