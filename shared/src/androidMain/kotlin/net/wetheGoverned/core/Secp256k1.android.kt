package net.wetheGoverned.core

import java.math.BigInteger

actual class CivicBigInt(val value: BigInteger) {
    actual companion object {
        actual val ZERO = CivicBigInt(BigInteger.ZERO)
        actual val ONE = CivicBigInt(BigInteger.ONE)
        actual val SECP256K1_N = CivicBigInt(BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16))
        actual fun fromHex(hex: String) = CivicBigInt(BigInteger(hex, 16))
        actual fun fromLong(long: Long) = CivicBigInt(BigInteger.valueOf(long))
    }
    actual fun add(other: CivicBigInt) = CivicBigInt(value.add(other.value))
    actual fun multiply(other: CivicBigInt) = CivicBigInt(value.multiply(other.value))
    actual fun mod(m: CivicBigInt) = CivicBigInt(value.mod(m.value))
    actual fun toHex() = value.toString(16)
}
