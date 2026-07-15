package net.wetheGoverned.util

expect object AddressUtils {
    fun normalizeAddress(street: String, city: String, zip: String): String
    fun generateFingerprint(street: String, city: String, zip: String): String
}
