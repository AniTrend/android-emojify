package io.wax911.emojify.util

import io.wax911.emojify.model.Emoji
import io.wax911.emojify.util.tree.Matches
import io.wax911.emojify.util.tree.Node


class EmojiTree(emojis: Collection<Emoji>) {
    private val root = Node()

    init {
        emojis.forEach { emoji ->
            var tree: Node? = root
            emoji.unicode.toCharArray().forEach { c ->
                if (tree?.hasChild(c) == false)
                    tree?.addChild(c)
                tree = tree?.getChild(c)
            }
            tree?.emoji = emoji
        }
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
    fun isEmoji(sequence: CharArray?): Matches {
        if (sequence == null)
            return Matches.POSSIBLY

        var tree: Node? = root
        sequence.forEach { c ->
            if (tree?.hasChild(c) == false)
                return Matches.IMPOSSIBLE
            tree = tree?.getChild(c)
        }

        return if (tree?.isEndOfEmoji == true)
            Matches.EXACTLY
        else Matches.POSSIBLY
    }


    /**
     * Finds Emoji instance from emoji unicode
     *
     * @param unicode unicode of emoji to get
     *
     * @return Emoji instance if unicode matches and emoji, null otherwise.
     */
    fun getEmoji(unicode: String): Emoji? {
        var tree: Node? = root
        unicode.toCharArray().forEach { c ->
            if (tree?.hasChild(c) == false)
                return null
            tree = tree?.getChild(c)
        }
        return tree?.emoji
    }
}
