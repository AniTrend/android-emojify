package io.wax911.emojify.contract.model

import java.nio.charset.Charset

abstract class AbstractEmoji: IEmoji {
    val unicode: String by lazy {
        String(emoji.toByteArray(), Charset.forName("UTF-8"))
    }

    val htmlDec by lazy {
        html("&#%d;")
    }

    val htmlHex by lazy {
        html("&#x%x;")
    }

    private fun html(format: String): String {
        val stringLength = unicode.length
        val points = arrayOfNulls<String>(stringLength)
        var count = 0
        var offset = 0
        while (offset < stringLength) {
            val codePoint = unicode.codePointAt(offset)
            points[count++] = String.format(format, codePoint)
            offset += Character.charCount(codePoint)
        }
        return points.joinToString(limit = count, truncated = "", separator = "")
    }
}
