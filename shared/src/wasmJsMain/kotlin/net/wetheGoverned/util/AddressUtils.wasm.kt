package net.wetheGoverned.util

actual object AddressUtils {
    actual fun normalizeAddress(street: String, city: String, zip: String): String {
        return "${street.lowercase().trim()},${city.lowercase().trim()},${zip.trim().take(5)}"
    }

    actual fun generateFingerprint(street: String, city: String, zip: String): String {
        val normalized = normalizeAddress(street, city, zip)
        return "wasm_fp_" + normalized.hashCode().toString()
    }
}
