/*
 * Copyright 2024 AniTrend
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

package io.wax911.emojify.contract.model

import java.nio.charset.Charset

/**
 * Abstract class holding some logic.
 * To implement for parser-specific data representation with their annotation
 */
abstract class AbstractEmoji : IEmoji {
    override val unicode: String by lazy(LazyThreadSafetyMode.NONE) {
        String(emoji.toByteArray(), Charset.forName("UTF-8"))
    }

    override val htmlDec by lazy(LazyThreadSafetyMode.NONE) {
        html("&#%d;")
    }

    override val htmlHex by lazy(LazyThreadSafetyMode.NONE) {
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
