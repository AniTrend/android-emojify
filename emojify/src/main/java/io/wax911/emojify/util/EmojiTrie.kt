package io.wax911.emojify.util

import java.util.HashMap

import io.wax911.emojify.model.Emoji

class EmojiTrie(emojis: Collection<Emoji>) {
    private val root = Node()

    init {
        for (emoji in emojis) {
            var tree: Node? = root
            for (c in emoji.unicode.toCharArray()) {
                if (tree?.hasChild(c) == false) {
                    tree.addChild(c)
                }
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
     * &lt;li&gt;
     * Matches.EXACTLY if char sequence in its entirety is an emoji
     * &lt;/li&gt;
     * &lt;li&gt;
     * Matches.POSSIBLY if char sequence matches prefix of an emoji
     * &lt;/li&gt;
     * &lt;li&gt;
     * Matches.IMPOSSIBLE if char sequence matches no emoji or prefix of an
     * emoji
     * &lt;/li&gt;
     */
    fun isEmoji(sequence: CharArray?): Matches {
        if (sequence == null) {
            return Matches.POSSIBLY
        }

        var tree: Node? = root
        for (c in sequence) {
            if (tree?.hasChild(c) == false) {
                return Matches.IMPOSSIBLE
            }
            tree = tree?.getChild(c)
        }

        return if (tree?.isEndOfEmoji == true) Matches.EXACTLY else Matches.POSSIBLY
    }


    /**
     * Finds Emoji instance from emoji unicode
     * @param unicode unicode of emoji to get
     * @return Emoji instance if unicode matches and emoji, null otherwise.
     */
    fun getEmoji(unicode: String): Emoji? {
        var tree: Node? = root
        for (c in unicode.toCharArray()) {
            if (tree?.hasChild(c) == false) {
                return null
            }
            tree = tree?.getChild(c)
        }
        return tree?.emoji
    }

    enum class Matches {
        EXACTLY, POSSIBLY, IMPOSSIBLE;

        fun exactMatch(): Boolean {
            return this == EXACTLY
        }

        fun impossibleMatch(): Boolean {
            return this == IMPOSSIBLE
        }
    }

    inner class Node {
        private val children by lazy {
            HashMap<Char, Node>()
        }
        internal var emoji: Emoji? = null

        internal val isEndOfEmoji: Boolean
            get() = emoji != null

        internal fun hasChild(child: Char): Boolean {
            return children.containsKey(child)
        }

        internal fun addChild(child: Char) {
            children[child] = Node()
        }

        internal fun getChild(child: Char): Node? {
            return children[child]
        }
    }
}
