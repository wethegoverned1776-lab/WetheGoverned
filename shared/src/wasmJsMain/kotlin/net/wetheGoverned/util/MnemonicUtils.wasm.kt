package net.wetheGoverned.util

actual object MnemonicUtils {
    actual fun generateMnemonic(): String = "web mnemonic stub"
    actual fun validateMnemonic(mnemonic: String): Boolean = mnemonic.isNotBlank()
    actual fun generateSeed(mnemonic: String, passphrase: String): ByteArray = mnemonic.encodeToByteArray()
}
