/*
 * Copyright 2021 AniTrend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.wax911.emojify.parser.candidate

import io.wax911.emojify.contract.model.IEmoji
import io.wax911.emojify.parser.candidate.contract.ICandidate
import io.wax911.emojify.util.Fitzpatrick
import java.util.Locale

/**
 * A Unicode emoji match found while scanning text.
 *
 * @property emoji the matched emoji record, or null when the character sequence is not in the catalog
 * @property emojiStartIndex index of the first UTF-16 code unit of the base emoji in the input
 * @property fitzpatrick the recognized Fitzpatrick modifier, if one follows the base emoji
 * @property fitzpatrickType lowercase shortcode suffix for the recognized modifier, or an empty string
 * @property fitzpatrickUnicode Unicode characters for the recognized modifier, or an empty string
 * @property emojiEndIndex index immediately after the base emoji, excluding any modifier
 * @property fitzpatrickEndIndex index immediately after the base emoji and any recognized modifier
 * @param fitzpatrick the raw modifier text following the base emoji, if present
 */
class UnicodeCandidate internal constructor(
    override val emoji: IEmoji?,
    fitzpatrick: String?,
    val emojiStartIndex: Int,
) : ICandidate {
    /** Recognized Fitzpatrick modifier attached to the base emoji, if present. */
    override val fitzpatrick: Fitzpatrick? = Fitzpatrick.fitzpatrickFromUnicode(fitzpatrick)

    /** Lowercase shortcode suffix for the modifier, or an empty string when absent. */
    val fitzpatrickType: String
        get() =
            if (hasFitzpatrick()) {
                fitzpatrick?.name?.lowercase(Locale.ROOT) ?: ""
            } else {
                ""
            }

    /** Unicode characters for the modifier, or an empty string when absent. */
    val fitzpatrickUnicode: String
        get() =
            if (hasFitzpatrick()) {
                fitzpatrick?.unicode ?: ""
            } else {
                ""
            }

    /** Index immediately after the base emoji, excluding any modifier. */
    val emojiEndIndex: Int
        get() = emojiStartIndex + (emoji?.emoji?.length ?: 0)

    /** Index immediately after the base emoji and any recognized modifier. */
    val fitzpatrickEndIndex: Int
        get() = emojiEndIndex + if (fitzpatrick != null) 2 else 0

    /** Returns whether this match includes a recognized Fitzpatrick modifier. */
    fun hasFitzpatrick() = fitzpatrick != null
}
