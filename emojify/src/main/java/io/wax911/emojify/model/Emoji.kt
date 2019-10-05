package io.wax911.emojify.model

import com.google.gson.annotations.SerializedName
import io.wax911.emojify.util.Fitzpatrick
import java.nio.charset.Charset


data class Emoji (
        val description: String? = null,
        @SerializedName("supports_fitzpatrick")
        val supportsFitzpatrick: Boolean = false,
        val aliases: List<String>? = null,
        val tags: List<String>? = null,
        val emojiChar: String,
        val emoji: String,
        var unicode: String,
        var htmlDec: String,
        var htmlHex: String
) {

    internal fun initProperties() {
        runCatching {
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
            htmlDec = stringJoin(pointCodes, count)
            htmlHex = stringJoin(pointCodesHex, count)
        }.exceptionOrNull()?.printStackTrace()
    }

    /**
     * Method to replace String.join, since it was only introduced in java8
     *
     * @param array the array to be concatenated
     * @return concatenated String
     */
    private fun stringJoin(array: Array<String?>, count: Int): String {
        var joined = ""
        (0 until count).forEach { i ->
            joined += array[i]
        }
        return joined
    }

    /**
     * Returns the unicode representation of the emoji associated with the
     * provided Fitzpatrick modifier.
     *
     * If the modifier is null, then the result is similar to [Emoji.getUnicode]
     *
     * @param fitzpatrick the fitzpatrick modifier or null
     *
     * @return the unicode representation
     * @throws UnsupportedOperationException if the emoji doesn't support the Fitzpatrick modifiers
     */
    @Throws(UnsupportedOperationException::class)
    fun getUnicode(fitzpatrick: Fitzpatrick?): String {
        if (!supportsFitzpatrick)
            throw UnsupportedOperationException(
                    """
                        Cannot get the unicode with a fitzpatrick modifier, 
                        the emoji doesn't support fitzpatrick.
                    """.trimIndent()
            )
        else if (fitzpatrick == null)
            return unicode
        return unicode + fitzpatrick.unicode
    }

    override fun equals(other: Any?) = when (other) {
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
     * **Example: **
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