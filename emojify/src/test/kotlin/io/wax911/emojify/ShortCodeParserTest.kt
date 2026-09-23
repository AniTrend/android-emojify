/*
 * Copyright 2025 AniTrend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.wax911.emojify

import io.wax911.emojify.core.EmojiLoader
import io.wax911.emojify.parser.parseShortCodesToUnicode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShortCodeParserTest : EmojiLoader() {

    @Test
    fun parseShortCodesToUnicode_replacesOneTokenWithinText() {
        assertEquals("Hello 😄", emojiManager.parseShortCodesToUnicode("Hello :smile:"))
    }

    @Test
    fun parseShortCodesToUnicode_convertsMultipleTokensAndPreservesText() {
        assertEquals(
            "A 😄, then 😉!",
            emojiManager.parseShortCodesToUnicode("A :smile:, then :wink:!"),
        )
    }

    @Test
    fun parseShortCodesToUnicode_convertsAdjacentTokens() {
        assertEquals("😄😉", emojiManager.parseShortCodesToUnicode(":smile::wink:"))
    }

    @Test
    fun parseShortCodesToUnicode_preservesPlainTextAndIncompleteTokens() {
        listOf(
            "plain text",
            "unfinished :",
            "unfinished :smile",
            "::",
            "::smile",
        ).forEach { input ->
            assertEquals(input, emojiManager.parseShortCodesToUnicode(input))
        }
    }

    @Test
    fun parseShortCodesToUnicode_convertsLegacyOnlyShortCodes() {
        assertEquals(
            "\uD83C\uDFF4\uDB40\uDC75\uDB40\uDC73\uDB40\uDC74\uDB40\uDC78\uDB40\uDC7F",
            emojiManager.parseShortCodesToUnicode(":ustx:"),
        )
        assertEquals("🏚️", emojiManager.parseShortCodesToUnicode(":abandoned_house:"))
        assertEquals("🦙", emojiManager.parseShortCodesToUnicode(":alpaca:"))
        assertEquals("🏹", emojiManager.parseShortCodesToUnicode(":archery:"))
    }

    @Test
    fun parseShortCodesToUnicode_resolvesConflictedShortCodesToModernRecords() {
        assertEquals("🐈️", emojiManager.parseShortCodesToUnicode(":cat:"))
        assertEquals("🌤️", emojiManager.parseShortCodesToUnicode(":sunny:"))
    }

    @Test
    fun parseShortCodesToUnicode_composesEverySupportedFitzpatrickSuffix() {
        val base = emojiManager.getForShortCode("boy")!!.single().emoji
        val suffixes =
            listOf(
                "type_1_2" to "🏻",
                "type_3" to "🏼",
                "type_4" to "🏽",
                "type_5" to "🏾",
                "type_6" to "🏿",
            )

        suffixes.forEach { (suffix, modifier) ->
            val expected = base + modifier
            assertEquals(expected, emojiManager.parseShortCodesToUnicode(":boy|$suffix:"))
        }
        assertEquals("👦🏿", emojiManager.parseShortCodesToUnicode(":boy|type_6:"))
        assertEquals("👦🏿", emojiManager.parseShortCodesToUnicode(":boy|TyPe_6:"))
    }

    @Test
    fun parseShortCodesToUnicode_preservesInvalidOrUnsupportedFitzpatrickTokens() {
        listOf(
            ":grinning|type_6:",
            ":boy|type_1:",
            ":boy|type_6|type_3:",
        ).forEach { input ->
            assertEquals(input, emojiManager.parseShortCodesToUnicode(input))
        }
    }

    @Test
    fun parseShortCodesToUnicode_preservesWholeTokenWhenFitzpatrickSuffixIsInvalid() {
        assertEquals(
            "before :boy|type_1: after",
            emojiManager.parseShortCodesToUnicode("before :boy|type_1: after"),
        )
    }

    @Test
    fun parseShortCodesToUnicode_composesFitzpatrickForMergedCapableZombie() {
        val result = emojiManager.parseShortCodesToUnicode(":zombie|type_6:")

        assertEquals("🧟\uD83C\uDFFF", result)
        assertEquals(
            emojiManager.getForShortCode("zombie")!!.single().emoji + "\uD83C\uDFFF",
            result,
        )
    }

    @Test
    fun parseShortCodesToUnicode_preservesUnknownTokensInMixedText() {
        assertEquals(
            "start :not_a_real_shortcode: 😄 end",
            emojiManager.parseShortCodesToUnicode("start :not_a_real_shortcode: :smile: end"),
        )
    }

    @Test
    fun parseShortCodesToUnicode_preservesLeadingColonBeforeNestedToken() {
        assertEquals(":👦", emojiManager.parseShortCodesToUnicode("::boy:"))
    }

    @Test
    fun parseShortCodesToUnicode_convertsTokenAtEndOfString() {
        assertEquals("text 😄", emojiManager.parseShortCodesToUnicode("text :smile:"))
    }

    @Test
    fun parseShortCodesToUnicode_convertsTokenImmediatelyAfterNonSpaceText() {
        assertEquals("prefix😄suffix", emojiManager.parseShortCodesToUnicode("prefix:smile:suffix"))
    }

    @Test
    fun parseShortCodesToUnicode_composesEveryToneFormForEveryCapableShortCode() {
        val suffixes =
            listOf(
                "type_1_2" to "🏻",
                "type_3" to "🏼",
                "type_4" to "🏽",
                "type_5" to "🏾",
                "type_6" to "🏿",
            )
        var checked = 0

        emojiManager.emojiList
            .filter { it.supportsFitzpatrick }
            .forEach { emoji ->
                emoji.shortCodes.orEmpty().forEach { shortCode ->
                    suffixes.forEach { (suffix, modifier) ->
                        assertEquals(
                            "capable code '$shortCode' must compose for $suffix",
                            emoji.emoji + modifier,
                            emojiManager.parseShortCodesToUnicode(":$shortCode|$suffix:"),
                        )
                        checked++
                    }
                }
            }

        assertTrue(
            "tone-form coverage must span every capable code times 5 suffixes (found $checked)",
            checked >= 1_550,
        )
    }

    @Test
    fun parseShortCodesToUnicode_preservesEveryLoadedShortCodeMapping() {
        emojiManager.emojiList.forEach { emoji ->
            emoji.shortCodes.orEmpty().forEach { shortCode ->
                val matches = emojiManager.getForShortCode(shortCode)
                assertNotNull("short code '$shortCode' must be loaded", matches)
                assertTrue("short code '$shortCode' must resolve uniquely", matches!!.size == 1)
                assertEquals(
                    "short code '$shortCode' must parse to its record",
                    emoji.emoji,
                    emojiManager.parseShortCodesToUnicode(":$shortCode:"),
                )
            }
        }
    }
}
