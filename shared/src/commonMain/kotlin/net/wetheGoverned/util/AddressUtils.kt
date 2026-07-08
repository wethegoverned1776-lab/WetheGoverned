package net.wetheGoverned.util

import java.security.MessageDigest

object AddressUtils {

    /**
     * Normalizes a US address to prevent Sybil attacks via formatting variations.
     * Uses a multi-pass normalization strategy to handle common abbreviations and spacing.
     */
    fun normalizeAddress(street: String, city: String, zip: String): String {
        // Pass 1: Basic cleanup
        var cleanStreet = street.lowercase().trim()
            .replace(Regex("[^a-z0-9\\s]"), "") // Remove punctuation
            .replace(Regex("\\s+"), " ")      // Collapse whitespace

        // Pass 2: Street Suffixes (Standardized to full names for better matching)
        val suffixMap = mapOf(
            "st" to "street",
            "ave" to "avenue",
            "av" to "avenue",
            "rd" to "road",
            "blvd" to "boulevard",
            "ln" to "lane",
            "dr" to "drive",
            "ct" to "court",
            "pl" to "place",
            "pkwy" to "parkway",
            "cir" to "circle",
            "hwy" to "highway",
            "ste" to "suite",
            "apt" to "apartment"
        )

        val words = cleanStreet.split(" ").map { word ->
            suffixMap[word] ?: word
        }
        cleanStreet = words.joinToString(" ")

        // Pass 3: Directionals
        cleanStreet = cleanStreet
            .replace(Regex("\\bn\\b|\\bnorth\\b"), "north")
            .replace(Regex("\\bs\\b|\\bsouth\\b"), "south")
            .replace(Regex("\\be\\b|\\beast\\b"), "east")
            .replace(Regex("\\bw\\b|\\bwest\\b"), "west")
            .replace(Regex("\\bne\\b|\\bnortheast\\b"), "northeast")
            .replace(Regex("\\bnw\\b|\\bnorthwest\\b"), "northwest")
            .replace(Regex("\\bse\\b|\\bsoutheast\\b"), "southeast")
            .replace(Regex("\\bsw\\b|\\bsouthwest\\b"), "southwest")

        val cleanCity = city.lowercase().trim().replace(Regex("[^a-z\\s]"), "").replace(Regex("\\s+"), " ")
        val cleanZip = zip.trim().take(5) // Only first 5 digits of ZIP
        
        return "${cleanStreet},${cleanCity},${cleanZip}"
    }

    /**
     * Generates a unique SHA-256 fingerprint for a resident's physical address.
     */
    fun generateFingerprint(street: String, city: String, zip: String): String {
        val normalized = normalizeAddress(street, city, zip)
        val bytes = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
