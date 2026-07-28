package net.wetheGoverned.util

actual object AddressUtils {
    actual fun normalizeAddress(street: String, city: String, zip: String): String {
        return "${street.lowercase().trim()},${city.lowercase().trim()},${zip.trim().take(5)}"
    }

    actual fun generateFingerprint(street: String, city: String, zip: String): String {
        val normalized = normalizeAddress(street, city, zip)
        return "sha256_" + net.wetheGoverned.core.sha256(normalized)
    }
}
