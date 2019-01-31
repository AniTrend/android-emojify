package io.wax911.emojify.util

/**
 * Enum that represents the Fitzpatrick modifiers supported by the emojis.
 *
 * @param unicode The unicode representation of the Fitzpatrick modifier
 */
enum class Fitzpatrick constructor(val unicode: String) {
    /**
     * Fitzpatrick modifier of type 1/2 (pale white/white)
     */
    TYPE_1_2("\uD83C\uDFFB"),

    /**
     * Fitzpatrick modifier of type 3 (cream white)
     */
    TYPE_3("\uD83C\uDFFC"),

    /**
     * Fitzpatrick modifier of type 4 (moderate brown)
     */
    TYPE_4("\uD83C\uDFFD"),

    /**
     * Fitzpatrick modifier of type 5 (dark brown)
     */
    TYPE_5("\uD83C\uDFFE"),

    /**
     * Fitzpatrick modifier of type 6 (black)
     */
    TYPE_6("\uD83C\uDFFF");


    companion object {

        fun fitzpatrickFromUnicode(unicode: String?): Fitzpatrick? {
            for (v in values()) {
                if (v.unicode == unicode) {
                    return v
                }
            }
            return null
        }

        fun fitzpatrickFromType(type: String): Fitzpatrick? {
            return try {
                Fitzpatrick.valueOf(type.toUpperCase())
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                null
            }
        }
    }
}
