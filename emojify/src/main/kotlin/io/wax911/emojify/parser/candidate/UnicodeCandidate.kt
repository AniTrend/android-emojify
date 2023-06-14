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

import io.wax911.emojify.model.Emoji
import io.wax911.emojify.util.Fitzpatrick
import java.util.Locale

/**
 * Emoji candidate
 *
 * @param emoji
 * @param fitzpatrick
 * @param emojiStartIndex
 */
class UnicodeCandidate internal constructor(
    val emoji: Emoji?,
    fitzpatrick: String?,
    val emojiStartIndex: Int,
) {
    private val fitzpatrick: Fitzpatrick? = Fitzpatrick.fitzpatrickFromUnicode(fitzpatrick)

    val fitzpatrickType: String
        get() = if (hasFitzpatrick()) {
            fitzpatrick?.name?.lowercase(Locale.ROOT) ?: ""
        } else {
            ""
        }

    val fitzpatrickUnicode: String
        get() = if (hasFitzpatrick()) {
            fitzpatrick?.unicode ?: ""
        } else {
            ""
        }

    private val emojiEndIndex: Int
        get() = emojiStartIndex + (emoji?.unicode?.length ?: 0)

    val fitzpatrickEndIndex: Int
        get() = emojiEndIndex + if (fitzpatrick != null) 2 else 0

    fun hasFitzpatrick() = fitzpatrick != null
}
