package io.wax911.emojifysample

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI regression tests for the Tier 1 screen. Local-only: CI excludes :app.
 *
 * The `grinning face with smiling eyes` emoji round-trips as `&#128516;` (decimal) and
 * `&#x1f604;` (hexadecimal) per emojify/src/main/assets/emoticons/emoji.json.
 */
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val inputField = composeTestRule.onNode(hasSetTextAction())

    /**
     * Asserts the field's editable text only. [androidx.compose.ui.test.assertTextEquals]
     * compares the merged Text list, which also contains the label and placeholder.
     */
    private fun assertInputText(expected: String) {
        inputField.assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.EditableText,
                AnnotatedString(expected),
            ),
        )
    }

    @Test
    fun emojiButton_convertsHtmlEntityToUnicode() {
        inputField.performTextInput("&#128516;")
        composeTestRule.onNodeWithText("Emoji").performClick()
        assertInputText("\uD83D\uDE04")
    }

    @Test
    fun htmlButton_convertsUnicodeToDecimalHtmlEntity() {
        inputField.performTextInput("\uD83D\uDE04")
        composeTestRule.onNodeWithText("HTML").performClick()
        assertInputText("&#128516;")
    }

    @Test
    fun hexButton_convertsUnicodeToHexadecimalHtmlEntity() {
        inputField.performTextInput("\uD83D\uDE04")
        composeTestRule.onNodeWithText("Hex").performClick()
        assertInputText("&#x1f604;")
    }

    @Test
    fun conversionButton_withBlankInput_keepsInputUnchanged() {
        inputField.performTextInput("   ")
        composeTestRule.onNodeWithText("Emoji").performClick()
        assertInputText("   ")
    }

    @Test
    fun conversionButton_withClearedInput_keepsInputUnchanged() {
        inputField.performTextInput("hello")
        inputField.performTextClearance()
        composeTestRule.onNodeWithText("HTML").performClick()
        assertInputText("")
    }
}
