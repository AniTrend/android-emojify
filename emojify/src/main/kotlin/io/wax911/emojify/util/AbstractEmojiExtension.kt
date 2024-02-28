package io.wax911.emojify.util

import io.wax911.emojify.contract.model.AbstractEmoji

/**
 * Returns the unicode representation of the emoji associated with the
 * provided Fitzpatrick modifier.
 *
 * If the modifier is null, then the result is similar to [AbstractEmoji.unicode]
 *
 * @param fitzpatrick the fitzpatrick modifier or null
 *
 * @return the unicode representation
 *
 * @throws UnsupportedOperationException if the emoji doesn't support the Fitzpatrick modifiers
 */
@Throws(UnsupportedOperationException::class)
fun AbstractEmoji.getUnicode(fitzpatrick: Fitzpatrick?): String {
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
