/*
 * Copyright 2025 AniTrend
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

package io.wax911.emojify.parser

import io.wax911.emojify.EmojiManager
import io.wax911.emojify.parser.candidate.ShortCodeCandidate
import io.wax911.emojify.util.Fitzpatrick

/**
 * Replaces recognized shortcode tokens with their Unicode emoji.
 *
 * The parser accepts `:code:` tokens and supported `:code|type_N:` Fitzpatrick
 * forms. For example, `:smile:` becomes `😄` and `:boy|type_6:` becomes `👦🏿`.
 * The 1.x `parseToAliases` entry point remains available as a deprecated
 * forwarder to [parseToShortCodes]; 1.x "aliases" are now called shortCodes.
 * The supported suffixes are `type_1_2`, `type_3`, `type_4`, `type_5`, and
 * `type_6`; their spelling is case-insensitive. A suffix is applied only when
 * the shortcode's emoji supports skin-tone modifiers. Unknown or ambiguous codes,
 * malformed or incomplete tokens, invalid suffixes, and suffixes on emojis
 * without Fitzpatrick support are preserved unchanged. In particular,
 * `:boy|type_1:` remains intact instead of dropping its invalid suffix.
 * Ordinary text outside recognized tokens is preserved byte-for-byte.
 *
 * @param input text that may contain shortcode tokens
 * @return the input with recognized shortcode tokens replaced by Unicode emoji
 * @see parseToShortCodes
 * @since 2.3.0
 */
fun EmojiManager.parseShortCodesToUnicode(input: String): String {
    val output = StringBuilder(input.length)
    var index = 0

    while (index < input.length) {
        val candidate = shortCodeCandidateAt(input, index)
        if (candidate == null) {
            output.append(input[index])
            index++
        } else {
            output.append(candidate.emoji.emoji)
            candidate.fitzpatrick?.let { output.append(it.unicode) }
            index = candidate.endIndex + 1
        }
    }

    return output.toString()
}

internal fun EmojiManager.shortCodeCandidateAt(
    input: String,
    start: Int,
): ShortCodeCandidate? {
    if (start !in input.indices || input[start] != ':') return null

    val end = input.indexOf(':', start + 1)
    if (end < start + 2) return null

    val token = input.substring(start + 1, end)
    val separator = token.indexOf('|')
    val shortCode = if (separator == -1) token else token.substring(0, separator)
    if (shortCode.isEmpty()) return null

    val fitzpatrick =
        if (separator == -1) {
            null
        } else {
            val suffix = token.substring(separator + 1)
            if (Fitzpatrick.entries.none { it.name.equals(suffix, ignoreCase = true) }) return null
            Fitzpatrick.fitzpatrickFromType(suffix) ?: return null
        }

    val matches = getForShortCode(shortCode) ?: return null
    if (matches.size != 1) return null

    val emoji = matches.single()
    if (fitzpatrick != null && !emoji.supportsFitzpatrick) return null

    return ShortCodeCandidate(
        emoji = emoji,
        fitzpatrick = fitzpatrick,
        startIndex = start,
        endIndex = end,
    )
}
