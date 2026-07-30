package net.wetheGoverned.core

// Minimal stub for iOS compilation in lab environment.
// For production sync on iOS, implement a native wrapper for big integers.
actual class CivicBigInt(val value: Long) {
    actual companion object {
        actual val ZERO = CivicBigInt(0)
        actual val ONE = CivicBigInt(1)
        actual val SECP256K1_N = CivicBigInt(12345) // Placeholder
        actual fun fromHex(hex: String) = CivicBigInt(hex.hashCode().toLong()) // Placeholder
        actual fun fromLong(long: Long) = CivicBigInt(long)
    }
    actual fun add(other: CivicBigInt) = CivicBigInt(value + other.value)
    actual fun multiply(other: CivicBigInt) = CivicBigInt(value * other.value)
    actual fun mod(m: CivicBigInt) = CivicBigInt(value % m.value)
    actual fun toHex() = value.toString(16)
}
