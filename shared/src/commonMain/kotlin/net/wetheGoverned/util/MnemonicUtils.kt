package net.wetheGoverned.util

import java.security.SecureRandom
import org.web3j.crypto.MnemonicUtils as Web3jMnemonicUtils

object MnemonicUtils {

    /**
     * Generates a 12-word BIP-39 mnemonic phrase.
     * Uses Web3j's hardened implementation and the standard 2048-word list.
     */
    fun generateMnemonic(): String {
        val initialEntropy = ByteArray(16) // 128 bits of entropy
        SecureRandom().nextBytes(initialEntropy)
        return Web3jMnemonicUtils.generateMnemonic(initialEntropy)
    }

    /**
     * Validates a BIP-39 mnemonic phrase.
     */
    fun validateMnemonic(mnemonic: String): Boolean {
        return Web3jMnemonicUtils.validateMnemonic(mnemonic)
    }
    
    /**
     * Converts mnemonic to a seed for key derivation.
     */
    fun generateSeed(mnemonic: String, passphrase: String = ""): ByteArray {
        return Web3jMnemonicUtils.generateSeed(mnemonic, passphrase)
    }
}
