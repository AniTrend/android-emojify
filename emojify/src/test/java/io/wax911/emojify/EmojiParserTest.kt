package io.wax911.emojify

import io.wax911.emojify.model.Emoji
import io.wax911.emojify.parser.EmojiParser
import io.wax911.emojify.parser.EmojiParser.FitzpatrickAction
import io.wax911.emojify.util.Fitzpatrick
import org.junit.Assert.*
import org.junit.Test


class EmojiParserTest {

    @Test
    fun parseToAliases_replaces_the_emojis_by_one_of_their_aliases() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals(
                "An :grinning:awesome :smiley:string with a few :wink:emojis!",
                result
        )
    }

    @Test
    @Throws(Exception::class)
    fun replaceAllEmojis_replace_the_emojis_by_string() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = EmojiParser.replaceAllEmojis(str, ":)")

        // THEN
        assertEquals(
                "An :)awesome :)string with a few :)emojis!",
                result
        )
    }


    @Test
    fun parseToAliases_REPLACE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals(":boy|type_6:", result)
    }

    @Test
    fun parseToAliases_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = EmojiParser.parseToAliases(str, FitzpatrickAction.REMOVE)

        // THEN
        assertEquals(":boy:", result)
    }

    @Test
    fun parseToAliases_REMOVE_without_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66"

        // WHEN
        val result = EmojiParser.parseToAliases(str, FitzpatrickAction.REMOVE)

        // THEN
        assertEquals(":boy:", result)
    }

    @Test
    fun parseToAliases_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = EmojiParser.parseToAliases(str, FitzpatrickAction.IGNORE)

        // THEN
        assertEquals(":boy:\uD83C\uDFFF", result)
    }

    @Test
    fun parseToAliases_IGNORE_without_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66"

        // WHEN
        val result = EmojiParser.parseToAliases(str, FitzpatrickAction.IGNORE)

        // THEN
        assertEquals(":boy:", result)
    }

    @Test
    fun parseToAliases_with_long_overlapping_emoji() {
        // GIVEN
        val str = "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        //With greedy parsing, this will give :man::woman::boy:
        //THEN
        assertEquals(":family_man_woman_boy:", result)
    }

    @Test
    fun parseToAliases_continuous_non_overlapping_emojis() {
        // GIVEN
        val str = "\uD83D\uDC69\uD83D\uDC68\uD83D\uDC66"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        //THEN
        assertEquals(":woman::man::boy:", result)
    }

    @Test
    fun parseToHtmlDecimal_replaces_the_emojis_by_their_html_decimal_representation() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = EmojiParser.parseToHtmlDecimal(str)

        // THEN
        assertEquals(
                "An &#128512;awesome &#128515;string with a few &#128521;emojis!",
                result
        )
    }

    @Test
    fun parseToHtmlDecimal_PARSE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = EmojiParser.parseToHtmlDecimal(
                str,
                FitzpatrickAction.PARSE
        )

        // THEN
        assertEquals("&#128102;", result)
    }

    @Test
    fun parseToHtmlDecimal_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = EmojiParser.parseToHtmlDecimal(
                str,
                FitzpatrickAction.REMOVE
        )

        // THEN
        assertEquals("&#128102;", result)
    }

    @Test
    fun parseToHtmlDecimal_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = EmojiParser.parseToHtmlDecimal(
                str,
                FitzpatrickAction.IGNORE
        )

        // THEN
        assertEquals("&#128102;\uD83C\uDFFF", result)
    }

    @Test
    fun parseToHtmlHexadecimal_replaces_the_emojis_by_their_htm_hex_representation() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = EmojiParser.parseToHtmlHexadecimal(str)

        // THEN
        assertEquals(
                "An &#x1f600;awesome &#x1f603;string with a few &#x1f609;emojis!",
                result
        )
    }

    @Test
    fun parseToHtmlHexadecimal_PARSE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = EmojiParser.parseToHtmlHexadecimal(
                str,
                FitzpatrickAction.PARSE
        )

        // THEN
        assertEquals("&#x1f466;", result)
    }

    @Test
    fun parseToHtmlHexadecimal_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = EmojiParser.parseToHtmlHexadecimal(
                str,
                FitzpatrickAction.REMOVE
        )

        // THEN
        assertEquals("&#x1f466;", result)
    }

    @Test
    fun parseToHtmlHexadecimal_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "\uD83D\uDC66\uD83C\uDFFF"

        // WHEN
        val result = EmojiParser.parseToHtmlHexadecimal(
                str,
                FitzpatrickAction.IGNORE
        )

        // THEN
        assertEquals("&#x1f466;\uD83C\uDFFF", result)
    }

    @Test
    fun parseToUnicode_replaces_the_aliases_and_the_html_by_their_emoji() {
        // GIVEN
        val str = "An :grinning:awesome :smiley:string " + "&#128516;with a few &#x1f609;emojis!"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals("An 😀awesome 😃string 😄with a few 😉emojis!", result)
    }

    @Test
    fun parseToUnicode_with_the_thumbsup_emoji_replaces_the_alias_by_the_emoji() {
        // GIVEN
        val str = "An :+1:awesome :smiley:string " + "&#128516;with a few :wink:emojis!"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals(
                "An \uD83D\uDC4Dawesome 😃string 😄with a few 😉emojis!",
                result
        )
    }

    @Test
    fun parseToUnicode_with_the_thumbsdown_emoji_replaces_the_alias_by_the_emoji() {
        // GIVEN
        val str = "An :-1:awesome :smiley:string &#128516;" + "with a few :wink:emojis!"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals(
                "An \uD83D\uDC4Eawesome 😃string 😄with a few 😉emojis!",
                result
        )
    }

    @Test
    fun parseToUnicode_with_the_thumbsup_emoji_in_hex_replaces_the_alias_by_the_emoji() {
        // GIVEN
        val str = "An :+1:awesome :smiley:string &#x1f604;" + "with a few :wink:emojis!"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals(
                "An \uD83D\uDC4Dawesome 😃string 😄with a few 😉emojis!",
                result
        )
    }

    @Test
    fun parseToUnicode_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = ":boy|type_6:"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals("\uD83D\uDC66\uD83C\uDFFF", result)
    }

    @Test
    fun parseToUnicode_with_an_unsupported_fitzpatrick_modifier_doesnt_replace() {
        // GIVEN
        val str = ":grinning|type_6:"
        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals(str, result)
    }

    @Test
    fun getAliasCanditates_with_one_alias() {
        // GIVEN
        val str = "test :candidate: test"

        // WHEN
        val candidates = EmojiParser.getAliasCandidates(str)

        // THEN
        assertEquals(1, candidates.size)
        assertEquals("candidate", candidates[0].alias)
        assertNull(candidates[0].fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_one_alias_an_another_colon_after() {
        // GIVEN
        val str = "test :candidate: test:"

        // WHEN
        val candidates = EmojiParser.getAliasCandidates(str)

        // THEN
        assertEquals(1, candidates.size)
        assertEquals("candidate", candidates[0].alias)
        assertNull(candidates[0].fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_one_alias_an_another_colon_right_after() {
        // GIVEN
        val str = "test :candidate::test"

        // WHEN
        val candidates = EmojiParser.getAliasCandidates(str)

        // THEN
        assertEquals(1, candidates.size)
        assertEquals("candidate", candidates[0].alias)
        assertNull(candidates[0].fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_one_alias_an_another_colon_before_after() {
        // GIVEN
        val str = "test ::candidate: test"

        // WHEN
        val candidates = EmojiParser.getAliasCandidates(str)

        // THEN
        assertEquals(1, candidates.size)
        assertEquals("candidate", candidates[0].alias)
        assertNull(candidates[0].fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_two_aliases() {
        // GIVEN
        val str = "test :candi: :candidate: test"

        // WHEN
        val candidates = EmojiParser.getAliasCandidates(str)

        // THEN
        assertEquals(2, candidates.size)
        assertEquals("candi", candidates[0].alias)
        assertNull(candidates[0].fitzpatrick)
        assertEquals("candidate", candidates[1].alias)
        assertNull(candidates[1].fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_two_aliases_sharing_a_colon() {
        // GIVEN
        val str = "test :candi:candidate: test"

        // WHEN
        val candidates = EmojiParser.getAliasCandidates(str)

        // THEN
        assertEquals(2, candidates.size)
        assertEquals("candi", candidates[0].alias)
        assertNull(candidates[0].fitzpatrick)
        assertEquals("candidate", candidates[1].alias)
        assertNull(candidates[1].fitzpatrick)
    }

    @Test
    fun getAliasCanditates_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "test :candidate|type_3: test"

        // WHEN
        val candidates = EmojiParser.getAliasCandidates(str)

        // THEN
        assertEquals(1, candidates.size)
        assertEquals("candidate", candidates[0].alias)
        assertEquals(Fitzpatrick.TYPE_3, candidates[0].fitzpatrick)
    }

    @Test
    fun test_with_a_new_flag() {
        val input = "Cuba has a new flag! :cu:"
        val expected = "Cuba has a new flag! \uD83C\uDDE8\uD83C\uDDFA"

        assertEquals(expected, EmojiParser.parseToUnicode(input))
        assertEquals(input, EmojiParser.parseToAliases(expected))
    }

    @Test
    fun removeAllEmojis_removes_all_the_emojis_from_the_string() {
        // GIVEN
        val input = "An 😀awesome 😃string 😄with " + "a \uD83D\uDC66\uD83C\uDFFFfew 😉emojis!"

        // WHEN
        val result = EmojiParser.removeAllEmojis(input)

        // THEN
        val expected = "An awesome string with a few emojis!"
        assertEquals(expected, result)
    }

    @Test
    fun removeEmojis_only_removes_the_emojis_in_the_iterable_from_the_string() {
        // GIVEN
        val input = "An\uD83D\uDE03 awesome\uD83D\uDE04 string" + "\uD83D\uDC4D\uD83C\uDFFF with\uD83D\uDCAA\uD83C\uDFFD a few emojis!"

        val emojis = ArrayList<Emoji>()

        val smile = EmojiManager.getForAlias("smile")
        val onePlus = EmojiManager.getForAlias("+1")

        assertNotNull(smile)
        assertNotNull(onePlus)

        emojis.add(smile!!)
        emojis.add(onePlus!!)

        // WHEN
        val result = EmojiParser.removeEmojis(input, emojis)

        // THEN
        val expected = "An\uD83D\uDE03 awesome string with" + "\uD83D\uDCAA\uD83C\uDFFD a few emojis!"
        assertEquals(expected, result)
    }

    @Test
    fun removeAllEmojisExcept_removes_all_the_emojis_from_the_string_except_those_in_the_iterable() {
        // GIVEN
        val input = "An\uD83D\uDE03 awesome\uD83D\uDE04 string" + "\uD83D\uDC4D\uD83C\uDFFF with\uD83D\uDCAA\uD83C\uDFFD a few emojis!"

        val emojis = ArrayList<Emoji>()

        val smile = EmojiManager.getForAlias("smile")
        val onePlus = EmojiManager.getForAlias("+1")

        assertNotNull(smile)
        assertNotNull(onePlus)

        emojis.add(smile!!)
        emojis.add(onePlus!!)

        // WHEN
        val result = EmojiParser.removeAllEmojisExcept(input, emojis)

        // THEN
        val expected = "An awesome\uD83D\uDE04 string\uD83D\uDC4D\uD83C\uDFFF " + "with a few emojis!"
        assertEquals(expected, result)
    }

    @Test
    fun parseToUnicode_with_the_keycap_asterisk_emoji_replaces_the_alias_by_the_emoji() {
        // GIVEN
        val str = "Let's test the :keycap_asterisk: emoji and " + "its other alias :star_keycap:"

        // WHEN
        val result = EmojiParser.parseToUnicode(str)

        // THEN
        assertEquals("Let's test the *⃣ emoji and its other alias *⃣", result)
    }

    @Test
    fun parseToAliases_NG_and_nigeria() {
        // GIVEN
        val str = "Nigeria is 🇳🇬, NG is 🆖"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals("Nigeria is :ng:, NG is :squared_ng:", result)
    }

    @Test
    fun parseToAliases_couplekiss_woman_woman() {
        // GIVEN
        val str = "👩‍❤️‍💋‍👩"

        // WHEN
        val result = EmojiParser.parseToAliases(str)

        // THEN
        assertEquals(":couplekiss_woman_woman:", result)
    }

    @Test
    fun extractEmojis() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result = EmojiParser.extractEmojis(str)

        // THEN
        assertEquals("😀", result[0])
        assertEquals("😃", result[1])
        assertEquals("😉", result[2])

    }
}