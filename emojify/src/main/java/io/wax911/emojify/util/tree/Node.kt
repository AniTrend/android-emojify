package io.wax911.emojify.util.tree

import io.wax911.emojify.model.Emoji
import java.util.*

/**
 * Node representation of an emoji tree
 *
 * @see io.wax911.emojify.util.EmojiTree
 */
class Node {
    private val children = HashMap<Char, Node>()

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