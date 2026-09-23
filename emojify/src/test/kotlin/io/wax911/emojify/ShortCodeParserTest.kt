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
import io.wax911.emojify.contract.model.IEmoji
import io.wax911.emojify.parser.action.FitzpatrickAction
import io.wax911.emojify.parser.parseShortCodesToUnicode
import io.wax911.emojify.parser.parseToAliases
import io.wax911.emojify.parser.parseToShortCodes
import io.wax911.emojify.util.Fitzpatrick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShortCodeParserTest : EmojiLoader() {

    @Test
    fun parseToShortCodes_replacesOneEmojiWithinText() {
        assertEquals("Hello :smile:", emojiManager.parseToShortCodes("Hello 😄"))
    }

    @Test
    fun parseToShortCodes_convertsMultipleEmojisAndPreservesText() {
        assertEquals(
            "A :smile:, then :wink:!",
            emojiManager.parseToShortCodes("A 😄, then 😉!"),
        )
    }

    @Test
    fun parseToShortCodes_usesFirstGeneratedShortCodeAsCanonicalOutput() {
        assertEquals(":grinning:", emojiManager.parseToShortCodes("😀"))
        assertEquals(":smile:", emojiManager.parseToShortCodes("😄"))
    }

    @Test
    fun parseToShortCodes_convertsMultiCodePointAndDemotedCanonicalEmoji() {
        val texasFlag = "\uD83C\uDFF4\uDB40\uDC75\uDB40\uDC73\uDB40\uDC74\uDB40\uDC78\uDB40\uDC7F"

        assertEquals(":man_zombie:", emojiManager.parseToShortCodes("🧟‍♂️"))
        assertEquals(":ustx:", emojiManager.parseToShortCodes(texasFlag))
        assertEquals(":envelope:", emojiManager.parseToShortCodes("✉️"))
        assertEquals(
            ":smiling_face_with_sunglasses:",
            emojiManager.parseToShortCodes("😎"),
        )
    }

    @Test
    fun parseToShortCodes_preservesEmojiWithNullOrEmptyShortCodeMetadata() {
        val withoutShortCodes = "🪅"
        val withEmptyShortCodes = "🪬"
        val manager =
            EmojiManager(
                listOf(
                    TestEmoji(withoutShortCodes, null),
                    TestEmoji(withEmptyShortCodes, emptyList()),
                ),
            )

        assertEquals(
            "$withoutShortCodes $withEmptyShortCodes",
            manager.parseToShortCodes("$withoutShortCodes $withEmptyShortCodes"),
        )
    }

    @Test
    fun parseToShortCodes_roundTripsEveryLoadedCatalogRecord() {
        emojiManager.emojiList.forEach { record ->
            val expected = record.shortCodes?.firstOrNull()?.let { ":$it:" } ?: record.emoji
            val shortCode = emojiManager.parseToShortCodes(record.emoji)

            assertEquals(
                "canonical shortcode output for '${record.description}'",
                expected,
                shortCode,
            )
            assertEquals(
                "shortcode round trip for '${record.description}'",
                record.emoji,
                emojiManager.parseShortCodesToUnicode(shortCode),
            )
        }
    }

    @Test
    fun parseToShortCodes_appliesEveryFitzpatrickActionForAllModifiersAndRoundTrips() {
        Fitzpatrick.entries.forEach { fitzpatrick ->
            val input = "👦${fitzpatrick.unicode}"
            val suffix = fitzpatrick.name.lowercase()
            val parsed = emojiManager.parseToShortCodes(input, FitzpatrickAction.PARSE)
            val removed = emojiManager.parseToShortCodes(input, FitzpatrickAction.REMOVE)
            val ignored = emojiManager.parseToShortCodes(input, FitzpatrickAction.IGNORE)

            assertEquals(":boy|$suffix:", parsed)
            assertEquals(":boy:", removed)
            assertEquals(":boy:${fitzpatrick.unicode}", ignored)
            assertEquals(input, emojiManager.parseShortCodesToUnicode(parsed))
            assertEquals("👦", emojiManager.parseShortCodesToUnicode(removed))
            assertEquals(input, emojiManager.parseShortCodesToUnicode(ignored))
        }

        assertEquals(":boy|type_6:", emojiManager.parseToShortCodes("👦🏿"))
        assertEquals(":boy:", emojiManager.parseToShortCodes("👦🏿", FitzpatrickAction.REMOVE))
        assertEquals(":boy:🏿", emojiManager.parseToShortCodes("👦🏿", FitzpatrickAction.IGNORE))
    }

    @Test
    fun parseToShortCodes_PARSE_nonCapableModifier_preservesRoundTripUnlike197Suffix() {
        val input = "😄🏿"
        val parsed = emojiManager.parseToShortCodes(input, FitzpatrickAction.PARSE)
        val removed = emojiManager.parseToShortCodes(input, FitzpatrickAction.REMOVE)
        val ignored = emojiManager.parseToShortCodes(input, FitzpatrickAction.IGNORE)

        // The 1.9.7 transformer suffixed every modifier, even on non-capable
        // records. The shortcode parser preserves that unsupported suffix as text.
        assertEquals(":smile:🏿", parsed)
        assertEquals(input, emojiManager.parseShortCodesToUnicode(parsed))
        assertEquals(":smile:", removed)
        assertEquals("😄", emojiManager.parseShortCodesToUnicode(removed))
        assertEquals(":smile:🏿", ignored)
        assertEquals(input, emojiManager.parseShortCodesToUnicode(ignored))
    }

    @Suppress("DEPRECATION")
    @Test
    fun parseToAliases_matchesShortCodesForRepresentativeInputs() {
        listOf("plain text" to "plain text", "Hello 😄" to "Hello :smile:")
            .forEach { (input, expected) ->
                assertEquals(expected, emojiManager.parseToAliases(input))
                assertEquals(emojiManager.parseToShortCodes(input), emojiManager.parseToAliases(input))
            }

        listOf(
            FitzpatrickAction.PARSE to ":boy|type_6:",
            FitzpatrickAction.REMOVE to ":boy:",
            FitzpatrickAction.IGNORE to ":boy:🏿",
        ).forEach { (action, expected) ->
            assertEquals(expected, emojiManager.parseToAliases("👦🏿", action))
            assertEquals(
                emojiManager.parseToShortCodes("👦🏿", action),
                emojiManager.parseToAliases("👦🏿", action),
            )
        }

        assertEquals(":smile:🏿", emojiManager.parseToAliases("😄🏿", FitzpatrickAction.PARSE))
        assertEquals(
            emojiManager.parseToShortCodes("😄🏿", FitzpatrickAction.PARSE),
            emojiManager.parseToAliases("😄🏿", FitzpatrickAction.PARSE),
        )
    }

    @Suppress("DEPRECATION")
    @Test
    fun parseToAliases_supportsDefaultAndExplicitFitzpatrickActionArguments() {
        val input = "👦🏿"

        assertEquals(":boy|type_6:", emojiManager.parseToAliases(input))
        assertEquals(":boy:🏿", emojiManager.parseToAliases(input, FitzpatrickAction.IGNORE))
    }

    @Suppress("DEPRECATION")
    @Test
    fun parseToAliases_roundTripsFitzpatrickEmoji() {
        assertEquals("👦🏿", emojiManager.parseShortCodesToUnicode(emojiManager.parseToAliases("👦🏿")))
    }

    private class TestEmoji(
        override val emoji: String,
        override val shortCodes: List<String>?,
    ) : IEmoji {
        override val description: String = "test emoji"
        override val supportsFitzpatrick: Boolean = false
        override val tags: List<String>? = null
        override val unicode: String = emoji
        override val htmlDec: String = ""
        override val htmlHex: String = ""
    }

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
    fun parseShortCodesToUnicode_composesAndEmitsEveryCapableCanonicalToneForm() {
        var checked = 0

        emojiManager.emojiList
            .filter { it.supportsFitzpatrick }
            .forEach { record ->
                val canonical =
                    requireNotNull(record.shortCodes?.firstOrNull()) {
                        "Fitzpatrick-capable record '${record.description}' must have a canonical short code"
                    }

                Fitzpatrick.entries.forEach { type ->
                    val shortCode = ":$canonical|${type.name.lowercase()}:"

                    assertEquals(
                        "incoming tone form '$shortCode' for '${record.description}'",
                        record.emoji + type.unicode,
                        emojiManager.parseShortCodesToUnicode(shortCode),
                    )
                    assertEquals(
                        "outgoing tone form for '${record.description}' and ${type.name}",
                        shortCode,
                        emojiManager.parseToShortCodes(
                            record.emoji + type.unicode,
                            FitzpatrickAction.PARSE,
                        ),
                    )
                    checked++
                }
            }

        assertTrue(
            "canonical tone-form coverage must include at least 310 capable codes times 5 Fitzpatrick spellings (found $checked)",
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
