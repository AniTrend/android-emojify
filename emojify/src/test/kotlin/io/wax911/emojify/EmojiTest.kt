package io.wax911.emojify

import io.wax911.emojify.core.EmojiLoader
import io.wax911.emojify.parser.action.FitzpatrickAction
import io.wax911.emojify.parser.candidate.UnicodeCandidate
import io.wax911.emojify.parser.parseFromUnicode
import io.wax911.emojify.parser.parseToAliases
import io.wax911.emojify.parser.parseToHtmlDecimal
import io.wax911.emojify.parser.parseToHtmlHexadecimal
import io.wax911.emojify.parser.parseToUnicode
import io.wax911.emojify.parser.transformer.EmojiTransformer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Arrays


@RunWith(value = Parameterized::class)
class EmojiTest(
    private val emoji: String,
    private val fitzpatrickAction: FitzpatrickAction,
) : EmojiLoader() {

    @Ignore("1665 emoji still has not been added")
    @Test
    fun checkEmojiExisting() {
        assertTrue("Asserting for emoji: $emoji", emojiManager.isEmoji(emoji))
    }

    @Test
    fun `check that emoji can be parsed to htmlDecimal`() {
        val htmlDecimal = emojiManager.parseToHtmlDecimal(emoji, fitzpatrickAction)
        val unicode = emojiManager.parseToUnicode(htmlDecimal)
        assertEquals(emoji, unicode)
    }

    @Test
    fun `check that emoji can be parsed to htmlHexadecimal`() {
        val htmlHexadecimal = emojiManager.parseToHtmlHexadecimal(emoji, fitzpatrickAction)
        val unicode = emojiManager.parseToUnicode(htmlHexadecimal)
        assertEquals(emoji, unicode)
    }

    @Test
    fun `check that emoji can be parsed to aliases`() {
        val alias = emojiManager.parseToAliases(emoji, fitzpatrickAction)
        val unicode = emojiManager.parseToUnicode(alias)
        assertEquals(emoji, unicode)
    }


    @Test
    fun `check emoji fitzpatrick flag`() {
        val len: Int = emoji.toCharArray().size
        var shouldContainFitzpatrick = false
        var codepoint: Int
        for (i in 0 until len) {
            codepoint = emoji.codePointAt(i)
            shouldContainFitzpatrick = Arrays.binarySearch(FITZPATRICK_CODEPOINTS, codepoint) >= 0
            if (shouldContainFitzpatrick) {
                break
            }
        }
        if (shouldContainFitzpatrick) {
            emojiManager.parseFromUnicode(
                emoji,
                object : EmojiTransformer {
                    override operator fun invoke(unicodeCandidate: UnicodeCandidate): String {
                        if (unicodeCandidate.hasFitzpatrick()) {
                            assertTrue(
                                "Asserting emoji contains fitzpatrick: " + emoji + " " + unicodeCandidate.emoji,
                                unicodeCandidate.emoji?.supportsFitzpatrick!!,
                            )
                        }
                        return ""
                    }
                },
            )
        }
    }

    companion object {
        val FITZPATRICK_CODEPOINTS = intArrayOf(
            convertFromCodepoint("1F3FB"),
            convertFromCodepoint("1F3FC"),
            convertFromCodepoint("1F3FD"),
            convertFromCodepoint("1F3FE"),
            convertFromCodepoint("1F3FF"),
        )

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: emoji = {0}")
        fun createEmojiList() =
            EmojiLoader::class.java.getResourceAsStream("emoji-test.txt")
                ?.use {
                    it.reader().readLines()
                }?.filter { line ->
                    !line.startsWith("#") &&
                        !line.startsWith(" ") &&
                        !line.startsWith("\n") &&
                        line.isNotEmpty()
                }?.map { line ->
                    val lineSplit = line.split(";")
                    convertToEmoji(lineSplit[0].trim())
                }

        private fun convertToEmoji(input: String): Array<Any> {
            val stringBuilder = StringBuilder()
            input.split(" ")
                .map { emojiCodepoint ->
                    val codePoint: Int = convertFromCodepoint(emojiCodepoint)
                    Character.toChars(codePoint)
                }
                .forEach(stringBuilder::append)

            return arrayOf(stringBuilder.toString(), FitzpatrickAction.IGNORE)
        }

        private fun convertFromCodepoint(emojiCodepointAsString: String): Int {
            return emojiCodepointAsString.toInt(16)
        }
    }
}
