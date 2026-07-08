package net.wetheGoverned.util

import jakarta.mail.*
import jakarta.mail.internet.*
import java.util.Properties

class DesktopEmailSender : EmailSender {
    // Configuration for your Internet-facing hMailServer or SMTP Provider
    private val smtpHost = "mail.wethegoverned.net" 
    private val smtpPort = "587"                    
    private val smtpUser = "verify@wethegoverned.net"
    private val smtpPass = "your_secure_password"
    private val fromEmail = "verify@wethegoverned.net"

    override suspend fun sendVerificationCode(email: String, code: String): Result<Unit> = runCatching {
        println("📡 [SMTP] Attempting to send code $code to $email via $smtpHost:$smtpPort...")
        
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", smtpHost)
            put("mail.smtp.port", smtpPort)
            put("mail.debug", "true") // Enable debug logging
            put("mail.smtp.timeout", "5000")
            put("mail.smtp.connectiontimeout", "5000")
            
            if (smtpPort == "465") {
                put("mail.smtp.socketFactory.port", smtpPort)
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                put("mail.smtp.starttls.enable", "false")
            }
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(smtpUser, smtpPass)
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(fromEmail, "WeTheGoverned Verify"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
            setSubject("Your WTG Verification Code")
            setText("Your 6-character verification code is: $code\n\nEnter this in the app to verify your identity.")
        }

        Transport.send(message)
    }
}

actual fun createEmailSender(): EmailSender = DesktopEmailSender()
