/*
 * Copyright 2021 AniTrend
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

import androidx.annotation.VisibleForTesting
import io.wax911.emojify.manager.IEmojiManager
import io.wax911.emojify.model.Emoji
import io.wax911.emojify.parser.getNextUnicodeCandidate
import io.wax911.emojify.parser.removeAllEmojis
import io.wax911.emojify.util.EmojiTrie
import io.wax911.emojify.util.trie.Matches

/**
 * Holds the loaded emojis and provides search functions.
 *
 * @param emojiList complete list of all emojis
 *
 * @author [Vincent DURMONT](vdurmont@gmail.com)
 */
class EmojiManager(
    override val emojiList: Collection<Emoji>
) : IEmojiManager {

    private val emojiByAlias by lazy {
        val aliasMap = HashMap<String, Emoji>()
        emojiList.forEach { emoji ->
            emoji.aliases?.forEach { alias ->
                aliasMap[alias] = emoji
            }
        }
        aliasMap
    }

    private val emojiByTag by lazy {
        val emojiTagMap = HashMap<String, MutableSet<Emoji>>()
        emojiList.forEach { emoji ->
            emoji.tags?.forEach { tag ->
                if (emojiTagMap[tag] == null)
                    emojiTagMap[tag] = HashSet()
                emojiTagMap[tag]?.add(emoji)
            }
        }
        emojiTagMap
    }

    private val emojiTrie: EmojiTrie by lazy {
        EmojiTrie(emojiList)
    }

    /**
     * Returns all the [Emoji]s for a given tag.
     *
     * @param tag the tag
     *
     * @return the associated [Emoji]s, null if the tag is unknown
     */
    override fun getForTag(tag: String?): Collection<Emoji>? =
        tag?.let { emojiByTag[it] }

    /**
     * Returns the [Emoji] for a given alias.
     *
     * @param alias the alias
     *
     * @return the associated [Emoji], null if the alias is unknown
     */
    override fun getForAlias(alias: String?): Emoji? =
        alias?.let { emojiByAlias[trimAlias(it)] }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun trimAlias(alias: String): String = alias.trim { it == ':' }

    /**
     * Returns the [Emoji] for a given unicode.
     *
     * @param unicode the the unicode
     *
     * @return the associated [Emoji], null if the unicode is unknown
     */
    override fun getByUnicode(unicode: String?): Emoji? =
        unicode?.let { emojiTrie.getEmoji(it) }

    /**
     * Tests if a given String is an emoji.
     *
     * @param string the string to test
     *
     * @return true if the string is an emoji's unicode, false else
     */
    override fun isEmoji(string: String?): Boolean {
        if (string == null) return false

        val unicodeCandidate = getNextUnicodeCandidate(string.toCharArray(), 0)
        return unicodeCandidate != null &&
            unicodeCandidate.emojiStartIndex == 0 &&
            unicodeCandidate.fitzpatrickEndIndex == string.length
    }

    /**
     * Tests if a given String only contains emojis.
     *
     * @param string the string to test
     *
     * @return true if the string only contains emojis, false else
     */
    override fun isOnlyEmojis(string: String?): Boolean {
        return string != null && removeAllEmojis(string).isEmpty()
    }

    /**
     * Checks if sequence of chars contain an emoji.
     *
     * @param sequence Sequence of char that may contain emoji in full or partially.
     *
     * @return
     * - [Matches.EXACTLY] if char sequence in its entirety is an emoji
     * - [Matches.POSSIBLY] if char sequence matches prefix of an emoji
     * - [Matches.IMPOSSIBLE] if char sequence matches no emoji or prefix of an emoji
     */
    override fun isEmoji(sequence: CharArray): Matches = emojiTrie.isEmoji(sequence)

    /**
     * Returns all the tags in the database
     *
     * @return the tags
     */
    override fun getAllTags(): Collection<String> = emojiByTag.keys
}
