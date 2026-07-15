package net.wetheGoverned.util

expect object MnemonicUtils {
    fun generateMnemonic(): String
    fun validateMnemonic(mnemonic: String): Boolean
    fun generateSeed(mnemonic: String, passphrase: String = ""): ByteArray
}
