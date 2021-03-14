package io.wax911.emojify.manager

import io.wax911.emojify.model.Emoji
import io.wax911.emojify.util.tree.Matches

/**
 * Emoji manager contract
 *
 * @property emojiList collection of emojis
 */
interface IEmojiManager {
    val emojiList: Collection<Emoji>

    /**
     * Returns all the [Emoji]s for a given tag.
     *
     * @param tag the tag
     *
     * @return the associated [Emoji]s, null if the tag is unknown
     */
    fun getForTag(tag: String?): Collection<Emoji>?

    /**
     * Returns the [Emoji] for a given alias.
     *
     * @param alias the alias
     *
     * @return the associated [Emoji], null if the alias is unknown
     */
    fun getForAlias(alias: String?): Emoji?

    /**
     * Returns the [Emoji] for a given unicode.
     *
     * @param unicode the the unicode
     *
     * @return the associated [Emoji], null if the unicode is unknown
     */
    fun getByUnicode(unicode: String?): Emoji?

    /**
     * Tests if a given String is an emoji.
     *
     * @param string the string to test
     *
     * @return true if the string is an emoji's unicode, false else
     */
    fun isEmoji(string: String?): Boolean

    /**
     * Tests if a given String only contains emojis.
     *
     * @param string the string to test
     *
     * @return true if the string only contains emojis, false else
     */
    fun isOnlyEmojis(string: String?): Boolean

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
    fun isEmoji(sequence: CharArray): Matches

    /**
     * Returns all the tags in the database
     *
     * @return the tags
     */
    fun getAllTags(): Collection<String>
}