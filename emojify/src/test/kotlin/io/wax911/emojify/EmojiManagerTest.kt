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

import io.wax911.emojify.contract.model.AbstractEmoji
import io.wax911.emojify.core.EmojiLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EmojiManagerTest : EmojiLoader() {

    @Test
    fun getTrimmedAlias() {
        // GIVEN
        val alias = ":smile:"

        // WHEN
        val trimmed = emojiManager.trimAlias(alias)

        // THEN
        assertEquals("smile", trimmed)
    }

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
        val emojis = emojiManager.getForTag("happy")

        // THEN
        assertEquals(4, emojis!!.size)
        assertTrue(
            emojis.containsAliases(
                "smile",
                "smiley",
                "grinning",
                "satisfied",
            ),
        )
    }

    @Test
    fun getForAlias_with_unknown_alias_returns_null() {
        // GIVEN

        // WHEN
        val emoji = emojiManager.getForAlias("jkahsgdfjksghfjkshf")

        // THEN
        assertNull(emoji)
    }

    @Test
    fun getForAlias_returns_the_emoji_for_the_alias() {
        // GIVEN

        // WHEN
        val emoji = emojiManager.getForAlias("smile")

        // THEN
        assertEquals(
            "smiling face with open mouth and smiling eyes",
            emoji!!.description,
        )
    }

    @Test
    fun getForAlias_with_colons_returns_the_emoji_for_the_alias() {
        // GIVEN

        // WHEN
        val emoji = emojiManager.getForAlias(":smile:")

        // THEN
        assertEquals(
            "smiling face with open mouth and smiling eyes",
            emoji!!.description,
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
        // We know the number of distinct tags int the...!
        assertEquals(656, tags.size)
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
                "Duplicate: " + emoji.description!!,
                unicodes.contains(emoji.unicode),
            )
            unicodes.add(emoji.unicode)
        }
        assertEquals(unicodes.size, emojis.size)
    }

    @Test
    fun no_duplicate_alias() {
        // GIVEN

        // WHEN
        val emojis = emojiManager.emojiList

        // THEN
        val aliases = HashSet<String>()
        val duplicates = HashSet<String>()
        for (emoji in emojis) {
            for (alias in emoji.aliases!!) {
                if (aliases.contains(alias)) {
                    duplicates.add(alias)
                }
                aliases.add(alias)
            }
        }
        assertEquals("Duplicates: $duplicates", duplicates.size, 0)
    }

    companion object {

        fun Iterable<AbstractEmoji>.containsAliases(vararg aliases: String) =
            aliases.any { alias ->
                mapNotNull(AbstractEmoji::aliases)
                    .toHashSet()
                    .any {
                        it.contains(alias)
                    }
            }
    }
}
