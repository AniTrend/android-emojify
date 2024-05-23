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

import android.content.Context
import android.content.res.AssetManager
import androidx.annotation.VisibleForTesting
import io.wax911.emojify.contract.model.IEmoji
import io.wax911.emojify.contract.serializer.IEmojiDeserializer
import io.wax911.emojify.contract.util.trie.Matches
import io.wax911.emojify.manager.IEmojiManager
import io.wax911.emojify.parser.nextUnicodeCandidate
import io.wax911.emojify.parser.removeAllEmojis
import io.wax911.emojify.util.EmojiTrie
import java.io.IOException

/**
 * Holds the loaded emojis and provides search functions.
 *
 * @param emojiList complete list of all emojis
 *
 * @author [Vincent DURMONT](vdurmont@gmail.com)
 */
class EmojiManager internal constructor(
    override val emojiList: Collection<IEmoji>,
) : IEmojiManager {
    private val emojiByAlias by lazy {
        val aliasMap = HashMap<String, IEmoji>()
        emojiList.forEach { emoji ->
            emoji.aliases?.forEach { alias ->
                aliasMap[alias] = emoji
            }
        }
        aliasMap
    }

    private val emojiByTag by lazy {
        val emojiTagMap = HashMap<String, MutableSet<IEmoji>>()
        emojiList.forEach { emoji ->
            emoji.tags?.forEach { tag ->
                if (emojiTagMap[tag] == null) {
                    emojiTagMap[tag] = HashSet()
                }
                emojiTagMap[tag]?.add(emoji)
            }
        }
        emojiTagMap
    }

    internal val emojiTrie: EmojiTrie by lazy {
        EmojiTrie(emojiList)
    }

    /**
     * Returns all the [IEmoji]s for a given tag.
     *
     * @param tag the tag
     *
     * @return the associated [IEmoji]s, null if the tag is unknown
     */
    override fun getForTag(tag: String?): Collection<IEmoji>? = tag?.let { emojiByTag[it] }

    /**
     * Returns the [IEmoji] for a given alias.
     *
     * @param alias the alias
     *
     * @return the associated [IEmoji], null if the alias is unknown
     */
    override fun getForAlias(alias: String?): IEmoji? = alias?.let { emojiByAlias[trimAlias(it)] }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun trimAlias(alias: String): String {
        val len = alias.length
        return alias.substring(
            if (alias.first() == ':') 1 else 0,
            if (alias.last() == ':') len - 1 else len,
        )
    }

    /**
     * Returns the [IEmoji] for a given unicode.
     *
     * @param unicode the the unicode
     *
     * @return the associated [IEmoji], null if the unicode is unknown
     */
    override fun getByUnicode(unicode: String?): IEmoji? = unicode?.let { emojiTrie.getEmoji(it.toCharArray()) }

    /**
     * Tests if a given String is an emoji.
     *
     * @param string the string to test
     *
     * @return true if the string is an emoji's unicode, false else
     */
    override fun isEmoji(string: String?): Boolean {
        if (string == null) return false

        val unicodeCandidate = nextUnicodeCandidate(string.toCharArray(), 0)
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

    companion object {
        private const val DEFAULT_EMOJI_PATH = "emoticons/emoji.json"

        @Throws(IOException::class)
        private fun initEmojiData(
            assetManager: AssetManager,
            serializer: IEmojiDeserializer,
            path: String = DEFAULT_EMOJI_PATH,
        ): List<IEmoji> =
            assetManager.open(path).use { inputStream ->
                serializer.decodeFromStream(inputStream)
            }

        /**
         * Initializes emoji objects from an asset file in the library directory
         *
         * @param context The application context.
         * @param serializer The implementation of a parser-specific
         *
         * @return [EmojiManager] In case of failure emojiList will be empty
         */
        fun create(
            context: Context,
            serializer: IEmojiDeserializer,
        ): EmojiManager {
            val result =
                runCatching {
                    val emojis = initEmojiData(context.assets, serializer)
                    EmojiManager(emojis)
                }.onFailure(Throwable::printStackTrace)
            return result.getOrElse { EmojiManager(emptyList()) }
        }
    }
}
