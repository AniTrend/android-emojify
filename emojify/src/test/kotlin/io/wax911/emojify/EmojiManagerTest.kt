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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EmojiManagerTest : EmojiLoader() {

    @Test
    fun getForTag_with_unknown_tag_returns_null() {
        // GIVEN

        // WHEN
        val emojis = emojiManager.getForTag("jkahsgdfjksghfjkshf")

        // THEN
        assertNull(emojis)
    }

    @Test
    fun getForTag_returns_the_emojis_for_the_tag() {
        // GIVEN

        // WHEN
        val emojis = emojiManager.getForTag("cheerful")

        // THEN
        assertEquals(1, emojis!!.size)
        assertEquals(
            listOf(
                "cheerful",
                "cheery",
                "face",
                "grin",
                "grinning",
                "happy",
                "laugh",
                "nice",
                "smile",
                "smiling",
                "teeth",
            ),
            emojis.mapNotNull(IEmoji::tags).flatten()
        )
    }

    @Test
    fun getForShortCode_with_unknown_shortcode_returns_null() {
        // GIVEN

        // WHEN
        val emojis = emojiManager.getForShortCode("unknown_shortcode_test")

        // THEN
        assertNull(emojis)
    }

    @Test
    fun getForShortCode_returns_the_emojis_for_the_shortcode() {
        // GIVEN
        val expectedEmojiUnicode = emojiManager.getForTag("cheerful")?.firstOrNull()?.unicode

        // WHEN
        val emojis = emojiManager.getForShortCode("grinning")

        // THEN
        assertTrue(!emojis.isNullOrEmpty())
        assertEquals(1, emojis!!.size) // Assuming "grinning" shortcode is unique or maps to one primary emoji here
        assertEquals(expectedEmojiUnicode, emojis.first().unicode)
        assertTrue(
            emojis.all { it.shortCodes?.contains("grinning") == true }
        )
    }

    @Test
    fun getForShortCode_with_colon_prefix_and_suffix_returns_the_emojis_for_the_shortcode() {
        // GIVEN
        val expectedEmojiUnicode = emojiManager.getForTag("cheerful")?.firstOrNull()?.unicode


        // WHEN
        val emojis = emojiManager.getForShortCode("grinning")

        // THEN
        assertTrue(!emojis.isNullOrEmpty())
        assertEquals(1, emojis!!.size)
        assertEquals(expectedEmojiUnicode, emojis.first().unicode)
        assertTrue(
            emojis.all { it.shortCodes?.contains("grinning") == true } // or ":grinning:" depending on your data
        )
    }


    @Test
    fun isEmoji_for_an_emoji_returns_true() {
        // GIVEN
        val emoji = "😀"

        // WHEN
        val isEmoji = emojiManager.isEmoji(emoji)

        // THEN
        assertTrue(isEmoji)
    }

    @Test
    fun isEmoji_with_fitzpatric_modifier_returns_true() {
        // GIVEN
        val emoji = "\uD83E\uDD30\uD83C\uDFFB"

        // WHEN
        val isEmoji = emojiManager.isEmoji(emoji)

        // THEN
        assertTrue(isEmoji)
    }

    @Test
    fun isEmoji_for_a_non_emoji_returns_false() {
        // GIVEN
        val str = "test"

        // WHEN
        val isEmoji = emojiManager.isEmoji(str)

        // THEN
        assertFalse(isEmoji)
    }

    @Test
    fun isEmoji_for_an_emoji_and_other_chars_returns_false() {
        // GIVEN
        val str = "😀 test"

        // WHEN
        val isEmoji = emojiManager.isEmoji(str)

        // THEN
        assertFalse(isEmoji)
    }

    @Test
    fun isOnlyEmojis_for_an_emoji_returns_true() {
        // GIVEN
        val str = "😀"

        // WHEN
        val isEmoji = emojiManager.isOnlyEmojis(str)

        // THEN
        assertTrue(isEmoji)
    }

    @Test
    fun isOnlyEmojis_for_emojis_returns_true() {
        // GIVEN
        val str = "😀😀😀"

        // WHEN
        val isEmoji = emojiManager.isOnlyEmojis(str)

        // THEN
        assertTrue(isEmoji)
    }

    @Test
    fun isOnlyEmojis_for_random_string_returns_false() {
        // GIVEN
        val str = "😀a"

        // WHEN
        val isEmoji = emojiManager.isOnlyEmojis(str)

        // THEN
        assertFalse(isEmoji)
    }

    @Test
    fun getAllTags_returns_the_tags() {
        // GIVEN

        // WHEN
        val tags = emojiManager.getAllTags()

        // THEN
        assertTrue(tags.isNotEmpty())
    }

    @Test
    fun getAll_does_not_return_duplicates() {
        // GIVEN

        // WHEN
        val emojis = emojiManager.emojiList

        // THEN
        val unicodes = HashSet<String>()
        for (emoji in emojis) {
            assertFalse(
                "Duplicate: " + emoji.description,
                unicodes.contains(emoji.unicode),
            )
            unicodes.add(emoji.unicode)
        }
        assertEquals(unicodes.size, emojis.size)
    }
}
