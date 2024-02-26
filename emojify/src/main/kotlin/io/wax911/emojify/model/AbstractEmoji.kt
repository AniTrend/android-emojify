package io.wax911.emojify.model

/**
 * @param aliases a list of aliases for this emoji
 * @param description the (optional) description of the emoji
 * @param emoji unicode emoji
 * @param emojiChar actual raw emoji
 * @param supportsFitzpatrick true if the emoji supports the Fitzpatrick modifiers, else false
 * @param supportsGender true if the emoji supports the gender modifiers, else false
 * @param tags a list of tags for this emoji
 */
abstract class AbstractEmoji(
    open val aliases: List<String>? = null,
    open val description: String? = null,
    open val emoji: String,
    open val emojiChar: String,
    open val supportsFitzpatrick: Boolean = false,
    open val supportsGender: Boolean = false,
    open val tags: List<String>? = null,
) {
    @Suppress("unused")
    constructor(): this(null, null, "", "", false, false, null)

    /**
     * Convert the parser-specific implementation to Emoji
     */
    fun toEmoji() = Emoji(aliases, description, emoji, emojiChar, supportsFitzpatrick, supportsGender, tags)
}
