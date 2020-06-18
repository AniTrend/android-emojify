package io.wax911.emojify.parser.candidate

import io.wax911.emojify.util.Fitzpatrick

internal class AliasCandidate internal constructor(
    val fullString: String,
    val alias: String,
    fitzpatrickString: String?
) {
    val fitzpatrick: Fitzpatrick?

    init {
        if (fitzpatrickString == null) {
            this.fitzpatrick = null
        } else {
            this.fitzpatrick = Fitzpatrick.fitzpatrickFromType(fitzpatrickString)
        }
    }
}