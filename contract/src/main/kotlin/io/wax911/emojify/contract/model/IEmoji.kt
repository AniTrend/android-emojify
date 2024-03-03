package io.wax911.emojify.contract.model

/**
 * @property aliases a list of aliases for this emoji
 * @property description the (optional) description of the emoji
 * @property emoji unicode emoji
 * @property emojiChar actual raw emoji
 * @property supportsFitzpatrick true if the emoji supports the Fitzpatrick modifiers, else false
 * @property supportsGender true if the emoji supports the gender modifiers, else false
 * @property tags a list of tags for this emoji
 */
internal interface IEmoji {
    val aliases: List<String>?
    val description: String?
    val emoji: String
    val emojiChar: String
    val supportsFitzpatrick: Boolean
    val supportsGender: Boolean
    val tags: List<String>?

    val unicode: String
    val htmlDec: String
    val htmlHex: String
}
