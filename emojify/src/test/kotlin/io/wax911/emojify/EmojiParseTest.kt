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

import io.wax911.emojify.contract.model.IEmoji
import io.wax911.emojify.core.EmojiLoader
import io.wax911.emojify.parser.action.FitzpatrickAction
import io.wax911.emojify.parser.extractEmojis
import io.wax911.emojify.parser.parseToHtmlDecimal
import io.wax911.emojify.parser.parseToHtmlHexadecimal
import io.wax911.emojify.parser.removeAllEmojis
import io.wax911.emojify.parser.removeAllEmojisExcept
import io.wax911.emojify.parser.removeEmojis
import io.wax911.emojify.parser.replaceAllEmojis
import org.junit.Assert.assertEquals
import org.junit.Test


class EmojiParseTest : EmojiLoader() {

    @Test
    @Throws(Exception::class)
    fun replaceAllEmojis_replace_the_emojis_by_string() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result: String = emojiManager.replaceAllEmojis(str, ":)")

        // THEN
        assertEquals(
            "An :)awesome :)string with a few :)emojis!",
            result,
        )
    }

    @Test
    fun parseToHtmlDecimal_replaces_the_emojis_by_their_html_decimal_representation() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result: String = emojiManager.parseToHtmlDecimal(str)

        // THEN
        assertEquals(
            "An &#128512;awesome &#128515;string with a few &#128521;emojis!",
            result,
        )
    }

    @Test
    fun parseToHtmlDecimal_PARSE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "👦🏿"

        // WHEN
        val result: String = emojiManager.parseToHtmlDecimal(
            str,
            FitzpatrickAction.PARSE,
        )

        // THEN
        assertEquals("&#128102;", result)
    }

    @Test
    fun parseToHtmlDecimal_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "👦🏿"

        // WHEN
        val result: String = emojiManager.parseToHtmlDecimal(
            str,
            FitzpatrickAction.REMOVE,
        )

        // THEN
        assertEquals("&#128102;", result)
    }

    @Test
    fun parseToHtmlDecimal_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "👦🏿"

        // WHEN
        val result: String = emojiManager.parseToHtmlDecimal(
            str,
            FitzpatrickAction.IGNORE,
        )

        // THEN
        assertEquals("&#128102;\uD83C\uDFFF", result)
    }

    @Test
    fun parseToHtmlHexadecimal_replaces_the_emojis_by_their_htm_hex_representation() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result: String = emojiManager.parseToHtmlHexadecimal(str)

        // THEN
        assertEquals(
            "An &#x1f600;awesome &#x1f603;string with a few &#x1f609;emojis!",
            result,
        )
    }

    @Test
    fun parseToHtmlHexadecimal_PARSE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "👦🏿"

        // WHEN
        val result: String = emojiManager.parseToHtmlHexadecimal(
            str,
            FitzpatrickAction.PARSE,
        )

        // THEN
        assertEquals("&#x1f466;", result)
    }

    @Test
    fun parseToHtmlHexadecimal_REMOVE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "👦🏿"

        // WHEN
        val result: String = emojiManager.parseToHtmlHexadecimal(
            str,
            FitzpatrickAction.REMOVE,
        )

        // THEN
        assertEquals("&#x1f466;", result)
    }

    @Test
    fun parseToHtmlHexadecimal_IGNORE_with_a_fitzpatrick_modifier() {
        // GIVEN
        val str = "👦🏿"

        // WHEN
        val result: String = emojiManager.parseToHtmlHexadecimal(
            str,
            FitzpatrickAction.IGNORE,
        )

        // THEN
        assertEquals("&#x1f466;\uD83C\uDFFF", result)
    }

    @Test
    fun removeAllEmojis_removes_all_the_emojis_from_the_string() {
        // GIVEN
        val input = "An 😀awesome 😃string 😄with a 👦🏿few 😉emojis!"

        // WHEN
        val result: String = emojiManager.removeAllEmojis(input)

        // THEN
        val expected = "An awesome string with a few emojis!"
        assertEquals(expected, result)
    }

    @Test
    fun removeEmojis_only_removes_the_emojis_in_the_iterable_from_the_string() {
        // GIVEN
        val input = "An😃 awesome😄 string 👍🏿 with💪🏽 a few emojis!"
        val emojis: List<IEmoji> = mutableListOf(
            emojiManager.emojiList.first { it.description == "grinning face with smiling eyes" },
            emojiManager.emojiList.first { it.description == "flexed biceps" }
        )

        // WHEN
        val result: String = emojiManager.removeEmojis(input, emojis)

        // THEN
        val expected = "An😃 awesome string 👍🏿 with a few emojis!"
        assertEquals(expected, result)
    }

    @Test
    fun removeAllEmojisExcept_removes_all_the_emojis_from_the_string_except_those_in_the_iterable() {
        // GIVEN
        val input = "An😃 awesome😄 string 👍 with💪🏽 a few emojis!"
        val emojis: MutableList<IEmoji> = ArrayList()
        emojis.add(emojiManager.emojiList.first { it.description == "grinning face with smiling eyes" })
        emojis.add(emojiManager.emojiList.first { it.description == "thumbs up" })


        // WHEN
        val result: String = emojiManager.removeAllEmojisExcept(input, emojis)

        // THEN
        val expected = "An awesome😄 string 👍 with a few emojis!"
        assertEquals(expected, result)
    }

    @Test
    fun extractEmojis() {
        // GIVEN
        val str = "An 😀awesome 😃string with a few 😉emojis!"

        // WHEN
        val result: List<String> = emojiManager.extractEmojis(str)

        // THEN
        assertEquals("😀", result[0])
        assertEquals("😃", result[1])
        assertEquals("😉", result[2])
    }
}
