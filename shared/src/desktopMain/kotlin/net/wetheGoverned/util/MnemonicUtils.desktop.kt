package net.wetheGoverned.util

import java.security.SecureRandom
import org.web3j.crypto.MnemonicUtils as Web3jMnemonicUtils

actual object MnemonicUtils {
    actual fun generateMnemonic(): String {
        val initialEntropy = ByteArray(16)
        SecureRandom().nextBytes(initialEntropy)
        return Web3jMnemonicUtils.generateMnemonic(initialEntropy)
    }

    actual fun validateMnemonic(mnemonic: String): Boolean = Web3jMnemonicUtils.validateMnemonic(mnemonic)
    actual fun generateSeed(mnemonic: String, passphrase: String): ByteArray = Web3jMnemonicUtils.generateSeed(mnemonic, passphrase)
}
