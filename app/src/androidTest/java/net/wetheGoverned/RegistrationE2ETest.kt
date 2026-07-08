package net.wetheGoverned

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class RegistrationE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSecureRegistrationAndMnemonicGeneration() {
        // 1. Switch to Registration Mode
        composeTestRule.onNodeWithText("Register").performClick()
        
        // 2. Verify we are in "Create Account" mode
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()

        // 3. Fill in registration details
        composeTestRule.onNodeWithText("Username").performTextInput("alice_resident")
        composeTestRule.onNodeWithText("Display Name").performTextInput("Alice Citizen")
        composeTestRule.onNodeWithText("Password").performTextInput("SecurePass123!")

        // 4. Submit Registration
        composeTestRule.onNodeWithText("Continue").performClick()

        // 5. Verify Mnemonic Display (Post-mitigation check)
        // Ensure the "IMPORTANT: Backup your Recovery Phrase" card appears
        composeTestRule.onNodeWithText("IMPORTANT: Backup your Recovery Phrase").assertIsDisplayed()
        
        // Check that a 12-word phrase is visible (Regex for 12 words)
        // This confirms MnemonicUtils.generateMnemonic() was called and the result is displayed
        composeTestRule.onNode(hasText(Regex("(\\w+\\s){11}\\w+").toPattern().toString())).assertExists()

        // 6. Confirm Backup
        composeTestRule.onNodeWithText("I have written it down").performClick()

        // 7. Verify we are back at Login (ready to use the new identity)
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }
}
