package net.wetheGoverned.core

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface DispatcherProvider {
    fun io(): CoroutineContext
    fun main(): CoroutineContext
    fun default(): CoroutineContext
}

class DefaultDispatcherProvider : DispatcherProvider {
    override fun io() = Dispatchers.Default 
    override fun main() = Dispatchers.Main
    override fun default() = Dispatchers.Default
}

object Secp256k1KeyManager {
    data class KeyPair(val pubKeyHex: String, val privateKeyHex: String)

    fun generateKeyPair(): KeyPair = KeyPair(
        pubKeyHex = "pub_" + (0..1000000).random().toString(),
        privateKeyHex = "priv_${(0..1000000).random()}",
    )

    fun deriveXOnlyPubKey(privKeyHex: String): String = "pub_" + privKeyHex.takeLast(8)
}

object Bech32Codec {
    fun encodeNsec(privKeyHex: String): String = "nsec1$privKeyHex"
    fun decodeNsec(nsec: String): String = nsec.removePrefix("nsec1")
}

fun formatDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${dateTime.dayOfMonth}, ${dateTime.year}"
}
