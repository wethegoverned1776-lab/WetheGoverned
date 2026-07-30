package net.wetheGoverned.core

@JsName("BigInt")
private external fun jsBigInt(s: String): JsAny

actual class CivicBigInt(val value: JsAny) {
    actual companion object {
        actual val ZERO = fromLong(0)
        actual val ONE = fromLong(1)
        actual val SECP256K1_N = fromHex("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141")
        actual fun fromHex(hex: String) = CivicBigInt(jsBigInt("0x$hex"))
        actual fun fromLong(long: Long) = CivicBigInt(jsBigInt(long.toString()))
    }
    actual fun add(other: CivicBigInt) = CivicBigInt(jsAdd(value, other.value))
    actual fun multiply(other: CivicBigInt) = CivicBigInt(jsMultiply(value, other.value))
    actual fun mod(m: CivicBigInt) = CivicBigInt(jsMod(value, m.value))
    actual fun toHex() = jsToHex(value)
}

private fun jsAdd(a: JsAny, b: JsAny): JsAny = js("a + b")
private fun jsMultiply(a: JsAny, b: JsAny): JsAny = js("a * b")
private fun jsMod(a: JsAny, b: JsAny): JsAny = js("a % b")
private fun jsToHex(a: JsAny): String = js("a.toString(16)")
