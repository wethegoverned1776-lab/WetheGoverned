package net.wetheGoverned.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MnemonicUtilsTest {

    @Test
    fun generateMnemonic_produces12Words() {
        val mnemonic = MnemonicUtils.generateMnemonic()
        val words = mnemonic.split(" ")
        assertEquals(12, words.size, "Mnemonic should have exactly 12 words")
    }

    @Test
    fun validateMnemonic_returnsTrueForValidMnemonic() {
        val mnemonic = MnemonicUtils.generateMnemonic()
        val isValid = MnemonicUtils.validateMnemonic(mnemonic)
        assertTrue(isValid, "Generated mnemonic should be valid")
    }

    @Test
    fun generateSeed_producesNonEmptyByteArray() {
        val mnemonic = MnemonicUtils.generateMnemonic()
        val seed = MnemonicUtils.generateSeed(mnemonic)
        assertTrue(seed.isNotEmpty(), "Seed should not be empty")
    }
}
