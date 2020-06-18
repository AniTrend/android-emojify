package io.wax911.emojify.parser.candidate

import io.wax911.emojify.model.Emoji
import io.wax911.emojify.util.Fitzpatrick
import java.util.*


internal class UnicodeCandidate internal constructor(
    val emoji: Emoji?,
    fitzpatrick: String?,
    val emojiStartIndex: Int
) {
    private val fitzpatrick: Fitzpatrick? = Fitzpatrick.fitzpatrickFromUnicode(fitzpatrick)

    val fitzpatrickType: String
        get() = if (hasFitzpatrick())
            fitzpatrick?.name?.toLowerCase(Locale.ROOT) ?: ""
        else ""

    val fitzpatrickUnicode: String
        get() = if (hasFitzpatrick())
            fitzpatrick?.unicode ?: ""
        else ""

    private val emojiEndIndex: Int
        get() = emojiStartIndex + (emoji?.unicode?.length ?: 0)

    val fitzpatrickEndIndex: Int
        get() = emojiEndIndex + if (fitzpatrick != null) 2 else 0

    fun hasFitzpatrick() =  fitzpatrick != null
}