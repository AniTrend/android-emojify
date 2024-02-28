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

package io.wax911.emojify.util

import io.wax911.emojify.contract.model.AbstractEmoji
import io.wax911.emojify.contract.util.trie.Matches
import io.wax911.emojify.util.trie.Node

class EmojiTrie(emojis: Collection<AbstractEmoji>) {
    private val root = Node()

    var maxDepth = 0

    init {
        var maximumDepth = 0
        emojis.forEach { emoji ->
            var tree: Node? = root
            val chars = emoji.unicode.toCharArray()
            maximumDepth = maximumDepth.coerceAtLeast(chars.size)
            chars.forEach { c ->
                if (tree?.hasChild(c) == false) {
                    tree?.addChild(c)
                }
                tree = tree?.getChild(c)
            }
            tree?.emoji = emoji
        }
        maxDepth = maximumDepth
    }

    /**
     * Checks if sequence of chars contain an emoji.
     *
     * @param sequence Sequence of char that may contain emoji in full or
     * partially.
     *
     * @return
     * - [Matches.EXACTLY] if char sequence in its entirety is an emoji
     * - [Matches.POSSIBLY] if char sequence matches prefix of an emoji
     * - [Matches.IMPOSSIBLE] if char sequence matches no emoji or prefix of an emoji
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    @JvmOverloads
    fun isEmoji(
        sequence: CharArray?,
        start: Int = 0,
        end: Int = sequence?.size ?: 0,
    ): Matches {
        if (sequence == null) {
            return Matches.POSSIBLY
        }

        if (start < 0 || start > end || end > sequence.size) {
            throw ArrayIndexOutOfBoundsException(
                "start " + start + ", end " + end + ", length " + sequence.size,
            )
        }

        var tree: Node? = root
        for (index in start until end) {
            if (tree?.hasChild(sequence[index]) == false) {
                return Matches.IMPOSSIBLE
            }
            tree = tree?.getChild(sequence[index])
        }

        return if (tree?.isEndOfEmoji == true) {
            Matches.EXACTLY
        } else {
            Matches.POSSIBLY
        }
    }

    /**
     * Finds Emoji instance from emoji unicode
     *
     * @param unicode unicode of emoji to get
     *
     * @return Emoji instance if unicode matches and emoji, null otherwise.
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    @JvmOverloads
    fun getEmoji(
        unicode: CharArray,
        start: Int = 0,
        end: Int = unicode.size,
    ): AbstractEmoji? {
        if (start < 0 || start > end || end > unicode.size) {
            throw ArrayIndexOutOfBoundsException(
                "start " + start + ", end " + end + ", length " + unicode.size,
            )
        }

        var tree: Node? = root
        for (index in start until end) {
            if (tree?.hasChild(unicode[index]) == false) {
                return null
            }
            tree = tree?.getChild(unicode[index])
        }
        return tree?.emoji
    }
}
