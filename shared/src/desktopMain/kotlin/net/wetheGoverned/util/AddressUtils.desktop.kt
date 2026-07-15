package net.wetheGoverned.util

import java.security.MessageDigest

actual object AddressUtils {
    actual fun normalizeAddress(street: String, city: String, zip: String): String {
        var cleanStreet = street.lowercase().trim().replace(Regex("[^a-z0-9\\s]"), "").replace(Regex("\\s+"), " ")
        val cleanCity = city.lowercase().trim().replace(Regex("[^a-z\\s]"), "").replace(Regex("\\s+"), " ")
        val cleanZip = zip.trim().take(5)
        return "${cleanStreet},${cleanCity},${cleanZip}"
    }

    actual fun generateFingerprint(street: String, city: String, zip: String): String {
        val normalized = normalizeAddress(street, city, zip)
        val bytes = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
