/*
 * Copyright 2023 AniTrend
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
import io.wax911.emojify.model.Emoji
import io.wax911.emojify.parser.action.FitzpatrickAction
import io.wax911.emojify.parser.extractEmojis
import io.wax911.emojify.parser.getAliasCandidates
import io.wax911.emojify.parser.parseToAliases
import io.wax911.emojify.parser.parseToHtmlDecimal
import io.wax911.emojify.parser.parseToHtmlHexadecimal
import io.wax911.emojify.parser.parseToUnicode
import io.wax911.emojify.parser.removeAllEmojis
import io.wax911.emojify.parser.removeAllEmojisExcept
import io.wax911.emojify.parser.removeEmojis
import io.wax911.emojify.parser.replaceAllEmojis
import io.wax911.emojify.util.Fitzpatrick
import org.junit.Assert
import org.junit.Test

class EmojiParseTest : EmojiLoader() {

    @Test
    fun parseToAliases_replaces_the_emojis_by_one_of_their_aliases() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = emojiManager.parseToAliases(str)

        // THEN
        Assert.assertEquals(
            "An :grinning:awesome :smiley:string with a few :wink:emojis!",
            result,
        )
    }

    @Test
    @Throws(Exception::class)
    fun replaceAllEmojis_replace_the_emojis_by_string() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = emojiManager.replaceAllEmojis(str, ":)")

        // THEN
        Assert.assertEquals(
            "An :)awesome :)string with a few :)emojis!",
            result,
        )
    }

    @Test
    fun parseToAliases_REPLACE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = emojiManager.parseToAliases(str)

        // THEN
        Assert.assertEquals(":boy|type_6:", result)
    }

    @Test
    fun parseToAliases_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = emojiManager.parseToAliases(str, FitzpatrickAction.REMOVE)

        // THEN
        Assert.assertEquals(":boy:", result)
    }

    @Test
    fun parseToAliases_REMOVE_without_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66"

        // WHEN
        val result = emojiManager.parseToAliases(str, FitzpatrickAction.REMOVE)

        // THEN
        Assert.assertEquals(":boy:", result)
    }

    @Test
    fun parseToAliases_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = emojiManager.parseToAliases(str, FitzpatrickAction.IGNORE)

        // THEN
        Assert.assertEquals(":boy:\uD83C\uDFFF", result)
    }

    @Test
    fun parseToAliases_IGNORE_without_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66"

        // WHEN
        val result = emojiManager.parseToAliases(str, FitzpatrickAction.IGNORE)

        // THEN
        Assert.assertEquals(":boy:", result)
    }

    @Test
    fun parseToAliases_with_long_overlapping_emoji() {
        // GIVEN
        val str = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66"

        // WHEN
        val result = emojiManager.parseToAliases(str)

        // With greedy parsing, this will give :man::woman::boy:
        // THEN
        Assert.assertEquals(":family_man_woman_boy:", result)
    }

    @Test
    fun parseToAliases_continuous_non_overlapping_emojis() {
        // GIVEN
        val str = "\uD83D\uDC69\uD83D\uDC68\uD83D\uDC66"

        // WHEN
        val result = emojiManager.parseToAliases(str)

        // THEN
        Assert.assertEquals(":woman::man::boy:", result)
    }

    @Test
    fun parseToHtmlDecimal_replaces_the_emojis_by_their_html_decimal_representation() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = emojiManager.parseToHtmlDecimal(str)

        // THEN
        Assert.assertEquals(
            "An &#128512;awesome &#128515;string with a few &#128521;emojis!",
            result,
        )
    }

    @Test
    fun parseToHtmlDecimal_PARSE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = emojiManager.parseToHtmlDecimal(
            str,
            FitzpatrickAction.PARSE,
        )

        // THEN
        Assert.assertEquals("&#128102;", result)
    }

    @Test
    fun parseToHtmlDecimal_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = emojiManager.parseToHtmlDecimal(
            str,
            FitzpatrickAction.REMOVE,
        )

        // THEN
        Assert.assertEquals("&#128102;", result)
    }

    @Test
    fun parseToHtmlDecimal_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = emojiManager.parseToHtmlDecimal(
            str,
            FitzpatrickAction.IGNORE,
        )

        // THEN
        Assert.assertEquals("&#128102;\uD83C\uDFFF", result)
    }

    @Test
    fun parseToHtmlHexadecimal_replaces_the_emojis_by_their_htm_hex_representation() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = emojiManager.parseToHtmlHexadecimal(str)

        // THEN
        Assert.assertEquals(
            "An &#x1f600;awesome &#x1f603;string with a few &#x1f609;emojis!",
            result,
        )
    }

    @Test
    fun parseToHtmlHexadecimal_PARSE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = emojiManager.parseToHtmlHexadecimal(
            str,
            FitzpatrickAction.PARSE,
        )

        // THEN
        Assert.assertEquals("&#x1f466;", result)
    }

    @Test
    fun parseToHtmlHexadecimal_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = emojiManager.parseToHtmlHexadecimal(
            str,
            FitzpatrickAction.REMOVE,
        )

        // THEN
        Assert.assertEquals("&#x1f466;", result)
    }

    @Test
    fun parseToHtmlHexadecimal_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = emojiManager.parseToHtmlHexadecimal(
            str,
            FitzpatrickAction.IGNORE,
        )

        // THEN
        Assert.assertEquals("&#x1f466;\uD83C\uDFFF", result)
    }

    @Test
    fun parseToUnicode_replaces_the_aliases_and_the_html_by_their_emoji() {
        // GIVEN
        val str = "An :grinning:awesome :smiley:string " + "&#128516;with a few &#x1f609;emojis!"

        // WHEN
        val result = emojiManager.parseToUnicode(str)

        // THEN
        Assert.assertEquals("An 😀awesome 😃string 😄with a few 😉emojis!", result)
    }

    @Test
    fun parseToUnicode_with_the_thumbs_up_emoji_replaces_the_alias_by_the_emoji() {
        // GIVEN
        val str = "An :+1:awesome :smiley:string " + "&#128516;with a few :wink:emojis!"

        // WHEN
        val result = emojiManager.parseToUnicode(str)

        // THEN
        Assert.assertEquals(
            "An \uD83D\uDC4Dawesome 😃string 😄with a few 😉emojis!",
            result,
        )
    }

    @Test
    fun parseToUnicode_with_the_thumbsdown_emoji_replaces_the_alias_by_the_emoji() {
        // GIVEN
        val str = "An :-1:awesome :smiley:string &#128516;" + "with a few :wink:emojis!"

        // WHEN
        val result = emojiManager.parseToUnicode(str)

        // THEN
        Assert.assertEquals(
            "An \uD83D\uDC4Eawesome 😃string 😄with a few 😉emojis!",
            result,
        )
    }

    @Test
    fun parseToUnicode_with_the_thumbs_up_emoji_in_hex_replaces_the_alias_by_the_emoji() {
        // GIVEN
        val str = "An :+1:awesome :smiley:string &#x1f604;" + "with a few :wink:emojis!"

        // WHEN
        val result = emojiManager.parseToUnicode(str)

        // THEN
        Assert.assertEquals(
            "An \uD83D\uDC4Dawesome 😃string 😄with a few 😉emojis!",
            result,
        )
    }

    @Test
    fun parseToUnicode_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = ":boy|type_6:"

        // WHEN
        val result = emojiManager.parseToUnicode(str)

        // THEN
        Assert.assertEquals("\uD83D\uDC66\uD83C\uDFFF", result)
    }

    @Test
    fun parseToUnicode_with_an_unsupported_fitzpatrick_modifier_does_not_replace() {
        // GIVEN
        val str = ":grinning|type_6:"
        // WHEN
        val result = emojiManager.parseToUnicode(str)

        // THEN
        Assert.assertEquals(str, result)
    }

    @Test
    fun getAliasCanditates_with_one_alias() {
        // GIVEN
        val str = "test :candidate: test"

        // WHEN
        val candidates = getAliasCandidates(str)

        // THEN
        Assert.assertEquals(1, candidates.size)
        Assert.assertEquals("candidate", candidates.first().alias)
        Assert.assertNull(candidates.first().fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_one_alias_an_another_colon_after() {
        // GIVEN
        val str = "test :candidate: test:"

        // WHEN
        val candidates = getAliasCandidates(str)

        // THEN
        Assert.assertEquals(1, candidates.size)
        Assert.assertEquals("candidate", candidates.first().alias)
        Assert.assertNull(candidates.first().fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_one_alias_an_another_colon_right_after() {
        // GIVEN
        val str = "test :candidate::test"

        // WHEN
        val candidates = getAliasCandidates(str)

        // THEN
        Assert.assertEquals(1, candidates.size)
        Assert.assertEquals("candidate", candidates.first().alias)
        Assert.assertNull(candidates.first().fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_one_alias_an_another_colon_before_after() {
        // GIVEN
        val str = "test ::candidate: test"

        // WHEN
        val candidates = getAliasCandidates(str)

        // THEN
        Assert.assertEquals(1, candidates.size)
        Assert.assertEquals("candidate", candidates.first().alias)
        Assert.assertNull(candidates.first().fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_two_aliases() {
        // GIVEN
        val str = "test :candi: :candidate: test"

        // WHEN
        val candidates = getAliasCandidates(str)

        // THEN
        Assert.assertEquals(2, candidates.size)
        Assert.assertEquals("candi", candidates.first().alias)
        Assert.assertNull(candidates.first().fitzpatrick)
        Assert.assertEquals("candidate", candidates[1].alias)
        Assert.assertNull(candidates[1].fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_two_aliases_sharing_a_colon() {
        // GIVEN
        val str = "test :candi:candidate: test"

        // WHEN
        val candidates = getAliasCandidates(str)

        // THEN
        Assert.assertEquals(2, candidates.size)
        Assert.assertEquals("candi", candidates.first().alias)
        Assert.assertNull(candidates.first().fitzpatrick)
        Assert.assertEquals("candidate", candidates[1].alias)
        Assert.assertNull(candidates[1].fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "test :candidate|type_3: test"

        // WHEN
        val candidates = getAliasCandidates(str)

        // THEN
        Assert.assertEquals(1, candidates.size)
        Assert.assertEquals("candidate", candidates.first().alias)
        Assert.assertEquals(Fitzpatrick.TYPE_3, candidates.first().fitzpatrick)
    }

    @Test
    fun test_with_a_new_flag() {
        val input = "Cuba has a new flag! :cu:"
        val expected = "Cuba has a new flag! \uD83C\uDDE8\uD83C\uDDFA"

        Assert.assertEquals(expected, emojiManager.parseToUnicode(input))
        Assert.assertEquals(input, emojiManager.parseToAliases(expected))
    }

    @Test
    fun removeAllEmojis_removes_all_the_emojis_from_the_string() {
        // GIVEN
        val input = "An 😀awesome 😃string 😄with " + "a \uD83D\uDC66\uD83C\uDFFFfew 😉emojis!"

        // WHEN
        val result = emojiManager.removeAllEmojis(input)

        // THEN
        val expected = "An awesome string with a few emojis!"
        Assert.assertEquals(expected, result)
    }

    @Test
    fun removeEmojis_only_removes_the_emojis_in_the_iterable_from_the_string() {
        // GIVEN
        val input = "An\uD83D\uDE03 awesome\uD83D\uDE04 string" + "\uD83D\uDC4D\uD83C\uDFFF with\uD83D\uDCAA\uD83C\uDFFD a few emojis!"

        val emojis = ArrayList<Emoji>()

        val smile = emojiManager.getForAlias("smile")
        val onePlus = emojiManager.getForAlias("+1")

        Assert.assertNotNull(smile)
        Assert.assertNotNull(onePlus)

        emojis.add(smile!!)
        emojis.add(onePlus!!)

        // WHEN
        val result = emojiManager.removeEmojis(input, emojis)

        // THEN
        val expected = "An\uD83D\uDE03 awesome string with" + "\uD83D\uDCAA\uD83C\uDFFD a few emojis!"
        Assert.assertEquals(expected, result)
    }

    @Test
    fun removeAllEmojisExcept_removes_all_the_emojis_from_the_string_except_those_in_the_iterable() {
        // GIVEN
        val input = "An\uD83D\uDE03 awesome\uD83D\uDE04 string" + "\uD83D\uDC4D\uD83C\uDFFF with\uD83D\uDCAA\uD83C\uDFFD a few emojis!"

        val emojis = ArrayList<Emoji>()

        val smile = emojiManager.getForAlias("smile")
        val onePlus = emojiManager.getForAlias("+1")

        Assert.assertNotNull(smile)
        Assert.assertNotNull(onePlus)

        emojis.add(smile!!)
        emojis.add(onePlus!!)

        // WHEN
        val result = emojiManager.removeAllEmojisExcept(input, emojis)

        // THEN
        val expected = "An awesome\uD83D\uDE04 string\uD83D\uDC4D\uD83C\uDFFF " + "with a few emojis!"
        Assert.assertEquals(expected, result)
    }

    @Test
    fun parseToUnicode_with_the_keycap_asterisk_emoji_replaces_the_alias_by_the_emoji() {
        // GIVEN
        val str = "Let's test the :keycap_asterisk: emoji and " + "its other alias :star_keycap:"

        // WHEN
        val result = emojiManager.parseToUnicode(str)

        // THEN
        Assert.assertEquals("Let's test the *⃣ emoji and its other alias *⃣", result)
    }

    @Test
    fun parseToAliases_NG_and_nigeria() {
        // GIVEN
        val str = "Nigeria is 🇳🇬, NG is 🆖"

        // WHEN
        val result = emojiManager.parseToAliases(str)

        // THEN
        Assert.assertEquals("Nigeria is :ng:, NG is :squared_ng:", result)
    }

    @Test
    fun parseToAliases_couplekiss_woman_woman() {
        // GIVEN
        val str = "👩‍❤️‍💋‍👩"

        // WHEN
        val result = emojiManager.parseToAliases(str)

        // THEN
        Assert.assertEquals(":couplekiss_woman_woman:", result)
    }

    @Test
    fun extractEmojis() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = emojiManager.extractEmojis(str)

        // THEN
        Assert.assertEquals("😀", result[0])
        Assert.assertEquals("😃", result[1])
        Assert.assertEquals("😉", result[2])
    }
}
