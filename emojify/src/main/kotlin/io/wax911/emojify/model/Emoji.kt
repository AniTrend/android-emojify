package io.wax911.emojify.model

import io.wax911.emojify.util.Fitzpatrick
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * @param aliases a list of aliases for this emoji
 * @param description the (optional) description of the emoji
 * @param emoji unicode emoji
 * @param emojiChar actual raw emoji
 * @param supportsFitzpatrick true if the emoji supports the Fitzpatrick modifiers, else false
 * @param supportsGender true if the emoji supports the gender modifiers, else false
 * @param tags a list of tags for this emoji
 * @property unicode the unicode representation of the emoji
 * @property htmlDec an html decimal representation of the emoji
 * @property htmlHex an html decimal representation of the emoji
 */
data class Emoji(
    val aliases: List<String>? = null,
    val description: String? = null,
    val emoji: String,
    val emojiChar: String,
    val supportsFitzpatrick: Boolean = false,
    val supportsGender: Boolean = false,
    val tags: List<String>? = null,
) {
    var unicode: String = ""
    var htmlDec: String = ""
    var htmlHex: String = ""

    init {
        runCatching {
            /** Upon init, we initialize optional properties: [unicode], [htmlDec] and [htmlHex] */
            initializeProperties()
        }.onFailure {
            if (it is UnsupportedEncodingException) {
                throw RuntimeException(it)
            } else {
                it.printStackTrace()
            }
        }
    }

    @Throws(Exception::class)
    private fun initializeProperties() {
        var count = 0
        unicode = String(emoji.toByteArray(), Charset.forName("UTF-8"))
        val stringLength = unicode.length
        val pointCodes = arrayOfNulls<String>(stringLength)
        val pointCodesHex = arrayOfNulls<String>(stringLength)

        var offset = 0
        while (offset < stringLength) {
            val codePoint = unicode.codePointAt(offset)

            pointCodes[count] = String.format("&#%d;", codePoint)
            pointCodesHex[count++] = String.format("&#x%x;", codePoint)

            offset += Character.charCount(codePoint)
        }

        htmlDec = pointCodes.joinToString(limit = count, truncated = "", separator = "")
        htmlHex = pointCodesHex.joinToString(limit = count, truncated = "", separator = "")
    }

    /**
     * Returns the unicode representation of the emoji associated with the
     * provided Fitzpatrick modifier.
     *
     * If the modifier is null, then the result is similar to [unicode]
     *
     * @param fitzpatrick the fitzpatrick modifier or null
     *
     * @return the unicode representation
     *
     * @throws UnsupportedOperationException if the emoji doesn't support the Fitzpatrick modifiers
     */
    @Throws(UnsupportedOperationException::class)
    fun getUnicode(fitzpatrick: Fitzpatrick?): String {
        if (!supportsFitzpatrick) {
            throw UnsupportedOperationException(
                """
                Cannot get the unicode with a fitzpatrick modifier,
                the emoji doesn't support fitzpatrick.
                """.trimIndent(),
            )
        } else if (fitzpatrick == null) {
            return unicode
        }
        return unicode + fitzpatrick.unicode
    }

    override fun equals(other: Any?) =
        when (other) {
            is Emoji -> other.unicode == unicode
            else -> super.equals(other)
        }

    override fun hashCode(): Int {
        var result = description?.hashCode() ?: 0
        result = 31 * result + supportsFitzpatrick.hashCode()
        result = 31 * result + (aliases?.hashCode() ?: 0)
        result = 31 * result + (tags?.hashCode() ?: 0)
        result = 31 * result + emojiChar.hashCode()
        result = 31 * result + emoji.hashCode()
        result = 31 * result + unicode.hashCode()
        result = 31 * result + htmlDec.hashCode()
        result = 31 * result + htmlHex.hashCode()
        return result
    }

    /**
     * Returns the String representation of the Emoji object.
     *
     * **Example:**
     * ```json
     * Emoji {
     *      description: 'smiling face with open mouth and smiling eyes',
     *      supportsFitzpatrick: false,
     *      aliases: [smile],
     *      tags: [happy, joy, pleased],
     *      unicode: '😄',
     *      htmlDec: '&#128516;',
     *      htmlHex: '&#x1f604;'
     * }
     * ```
     *
     * @return the string representation
     */
    override fun toString(): String {
        return "Emoji{" +
            "description:'" + description + '\''.toString() +
            ", supportsFitzpatrick:" + supportsFitzpatrick +
            ", aliases:" + aliases +
            ", tags:" + tags +
            ", unicode:'" + unicode + '\''.toString() +
            ", htmlDec:'" + htmlDec + '\''.toString() +
            ", htmlHex:'" + htmlHex + '\''.toString() +
            '}'.toString()
    }
}
