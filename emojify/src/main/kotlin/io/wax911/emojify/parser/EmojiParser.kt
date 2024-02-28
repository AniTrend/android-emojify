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

package io.wax911.emojify.parser

import io.wax911.emojify.EmojiManager
import io.wax911.emojify.contract.model.AbstractEmoji
import io.wax911.emojify.parser.action.FitzpatrickAction
import io.wax911.emojify.parser.candidate.AliasCandidate
import io.wax911.emojify.parser.candidate.UnicodeCandidate
import io.wax911.emojify.parser.transformer.EmojiTransformer
import io.wax911.emojify.util.Fitzpatrick
import io.wax911.emojify.util.getUnicode

/**
 * Replaces the emoji's unicode occurrences by one of their alias
 * (between 2 ':').
 *
 * > `😄` will be replaced by `:smile:`
 *
 *
 * When a fitzpatrick modifier is present with a PARSE action, a "|" will be
 * appended to the alias, with the fitzpatrick type.
 *
 * > `👦🏿` will be replaced by `:boy|type_6:`
 *
 *
 * When a fitzpatrick modifier is present with a REMOVE action, the modifier
 * will be deleted.
 *
 * > `👦🏿` will be replaced by `:boy:`
 *
 *
 * When a fitzpatrick modifier is present with a IGNORE action, the modifier
 * will be ignored.
 *
 * > `👦🏿` will be replaced by `:boy:🏿`
 *
 * @param input             the string to parse
 * @param fitzpatrickAction the action to apply for the fitzpatrick modifiers
 *
 * @return the string with the emojis replaced by their alias.
 * @see io.wax911.emojify.util.Fitzpatrick
 */
@JvmOverloads
fun EmojiManager.parseToAliases(
    input: String,
    fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE,
): String {
    val emojiTransformer =
        object : EmojiTransformer {
            override fun invoke(unicodeCandidate: UnicodeCandidate): String {
                val alias = unicodeCandidate.emoji?.aliases?.get(0)
                val fitzpatrickType = unicodeCandidate.fitzpatrickType
                val fitzpatrickUnicode = unicodeCandidate.fitzpatrickUnicode
                when (fitzpatrickAction) {
                    FitzpatrickAction.PARSE -> {
                        return if (unicodeCandidate.hasFitzpatrick()) {
                            ":$alias|$fitzpatrickType:"
                        } else {
                            ":$alias:"
                        }
                    }

                    FitzpatrickAction.REMOVE -> return ":$alias:"
                    FitzpatrickAction.IGNORE -> return ":$alias:$fitzpatrickUnicode"
                    else -> {
                        return if (unicodeCandidate.hasFitzpatrick()) {
                            ":$alias|$fitzpatrickType:"
                        } else {
                            ":$alias:"
                        }
                    }
                }
            }
        }

    return parseFromUnicode(input, emojiTransformer)
}

/**
 * Replace all emojis with character
 *
 * @param str the string to process
 * @param replacementString replacement the string that will replace all the emojis
 * @return the string with replaced character
 */
fun EmojiManager.replaceAllEmojis(
    str: String,
    replacementString: String,
): String {
    val emojiTransformer =
        object : EmojiTransformer {
            override fun invoke(unicodeCandidate: UnicodeCandidate) = replacementString
        }

    return parseFromUnicode(str, emojiTransformer)
}

/**
 * Replaces the emoji's aliases (between 2 ':') occurrences and the html
 * representations by their unicode.
 *
 * > `:smile:` will be replaced by `😄`
 *
 * > `&#128516;` will be replaced by `😄`
 *
 * > `:boy|type_6:` will be replaced by `👦🏿`
 *
 * @param input the string to parse
 *
 * @return the string with the aliases and html representations replaced by
 * their unicode.
 */
fun EmojiManager.parseToUnicode(input: String): String {
    val sb = StringBuilder(input.length)

    var last = 0
    while (last < input.length) {
        var alias: AliasCandidate? = aliasCandidateAt(input, last)
        if (alias == null) {
            alias = htmlEncodedEmojiAt(input, last)
        }

        if (alias != null) {
            sb.append(alias.emoji.unicode)
            last = alias.endIndex

            if (alias.fitzpatrick != null) {
                sb.append(alias.fitzpatrick!!.unicode)
            }
        } else {
            sb.append(input[last])
        }
        last++
    }

    return sb.toString()
}

internal fun EmojiManager.aliasCandidateAt(
    input: String,
    start: Int,
): AliasCandidate? {
    if (input.length < start + 2 || input[start] != ':') return null; // Aliases start with :
    val aliasEnd: Int = input.indexOf(':', start + 2) // Alias must be at least 1 char in length
    if (aliasEnd == -1) return null // No alias end found

    val fitzpatrickStart: Int = input.indexOf('|', start + 2)
    if (fitzpatrickStart != -1 && fitzpatrickStart < aliasEnd) {
        val emoji = getForAlias(input.substring(start, fitzpatrickStart)) ?: return null // Not a valid alias
        if (!emoji.supportsFitzpatrick) return null // Fitzpatrick was specified, but the emoji does not support it
        val fitzpatrick =
            Fitzpatrick.fitzpatrickFromType(input.substring(fitzpatrickStart + 1, aliasEnd))
        return AliasCandidate(emoji, fitzpatrick, start, aliasEnd)
    }

    val emoji = getForAlias(input.substring(start, aliasEnd)) ?: return null // Not a valid alias

    return AliasCandidate(
        emoji = emoji,
        fitzpatrick = null,
        startIndex = start,
        endIndex = aliasEnd,
    )
}

internal fun EmojiManager.htmlEncodedEmojiAt(
    input: String,
    start: Int,
): AliasCandidate? {
    if (input.length < start + 4 || input[start] != '&' || input[start + 1] != '#') return null

    var longestEmoji: AbstractEmoji? = null
    var longestCodePointEnd = -1
    val chars = CharArray(emojiTrie.maxDepth)
    var charsIndex = 0
    var codePointStart = start
    do {
        val codePointEnd =
            input.indexOf(';', codePointStart + 3) // Code point must be at least 1 char in length
        if (codePointEnd == -1) break

        try {
            val radix = if (input[codePointStart + 2] == 'x') 16 else 10
            val codePoint =
                input.substring(codePointStart + 2 + radix / 16, codePointEnd).toInt(radix)
            charsIndex += Character.toChars(codePoint, chars, charsIndex)
        } catch (e: NumberFormatException) {
            break
        } catch (e: IllegalArgumentException) {
            break
        }
        val foundEmoji = emojiTrie.getEmoji(chars, 0, charsIndex)
        if (foundEmoji != null) {
            longestEmoji = foundEmoji
            longestCodePointEnd = codePointEnd
        }
        codePointStart = codePointEnd + 1
    } while (
        input.length > codePointStart + 4 &&
        input[codePointStart] == '&' &&
        input[codePointStart + 1] == '#' &&
        charsIndex < chars.size &&
        !emojiTrie.isEmoji(chars, 0, charsIndex).impossibleMatch()
    )

    if (longestEmoji == null) return null

    return AliasCandidate(
        emoji = longestEmoji,
        fitzpatrick = null,
        startIndex = start,
        endIndex = longestCodePointEnd,
    )
}

/**
 * Replaces the emoji's unicode occurrences by their html representation.
 *
 * > `😄` will be replaced by `&#128516;`
 *
 *
 * When a fitzpatrick modifier is present with a PARSE or REMOVE action, the
 * modifier will be deleted from the string.
 *
 * > `👦🏿` will be replaced by `&#128102;`
 *
 *
 * When a fitzpatrick modifier is present with a IGNORE action, the modifier
 * will be ignored and will remain in the string.
 *
 * > `👦🏿` will be replaced by `&#128102;🏿`
 *
 * @param input the string to parse
 * @param fitzpatrickAction the action to apply for the fitzpatrick modifiers
 *
 * @return the string with the emojis replaced by their html decimal
 * representation.
 */
@JvmOverloads
fun EmojiManager.parseToHtmlDecimal(
    input: String,
    fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE,
): String {
    val emojiTransformer =
        object : EmojiTransformer {
            override fun invoke(unicodeCandidate: UnicodeCandidate): String? {
                return when (fitzpatrickAction) {
                    FitzpatrickAction.PARSE,
                    FitzpatrickAction.REMOVE,
                    ->
                        unicodeCandidate.emoji?.htmlDec

                    FitzpatrickAction.IGNORE ->
                        unicodeCandidate.emoji?.htmlDec + unicodeCandidate.fitzpatrickUnicode
                }
            }
        }

    return parseFromUnicode(input, emojiTransformer)
}

/**
 * Replaces the emoji's unicode occurrences by their html hex representation.
 *
 * > '' will be replaced by `&#x1f466;`
 *
 *
 * When a fitzpatrick modifier is present with a PARSE or REMOVE action, the
 * modifier will be deleted.
 *
 * > `👦🏿` will be replaced by `&#x1f466;`
 *
 *
 * When a fitzpatrick modifier is present with a IGNORE action, the modifier
 * will be ignored and will remain in the string.
 *
 * > `👦🏿` will be replaced by `&#x1f466;🏿`
 *
 * @param input the string to parse
 * @param fitzpatrickAction the action to apply for the fitzpatrick modifiers
 *
 * @return the string with the emojis replaced by their html hex representation.
 */
@JvmOverloads
fun EmojiManager.parseToHtmlHexadecimal(
    input: String,
    fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE,
): String {
    val emojiTransformer =
        object : EmojiTransformer {
            override fun invoke(unicodeCandidate: UnicodeCandidate): String? {
                return when (fitzpatrickAction) {
                    FitzpatrickAction.PARSE,
                    FitzpatrickAction.REMOVE,
                    ->
                        unicodeCandidate.emoji?.htmlHex

                    FitzpatrickAction.IGNORE ->
                        unicodeCandidate.emoji?.htmlHex + unicodeCandidate.fitzpatrickUnicode
                }
            }
        }

    return parseFromUnicode(input, emojiTransformer)
}

/**
 * Removes all emojis from a String
 *
 * @param str the string to process
 *
 * @return the string without any emoji
 */
fun EmojiManager.removeAllEmojis(str: String): String {
    val emojiTransformer =
        object : EmojiTransformer {
            override fun invoke(unicodeCandidate: UnicodeCandidate) = ""
        }

    return parseFromUnicode(str, emojiTransformer)
}

/**
 * Removes a set of emojis from a String
 *
 * @param str the string to process
 * @param emojisToRemove the emojis to remove from this string
 *
 * @return the string without the emojis that were removed
 */
fun EmojiManager.removeEmojis(
    str: String,
    emojisToRemove: Collection<AbstractEmoji>,
): String {
    val emojiTransformer =
        object : EmojiTransformer {
            override fun invoke(unicodeCandidate: UnicodeCandidate): String {
                return if (!emojisToRemove.contains(unicodeCandidate.emoji)) {
                    unicodeCandidate.emoji?.unicode + unicodeCandidate.fitzpatrickUnicode
                } else {
                    ""
                }
            }
        }

    return parseFromUnicode(str, emojiTransformer)
}

/**
 * Removes all the emojis in a String except a provided set
 *
 * @param str the string to process
 * @param emojisToKeep the emojis to keep in this string
 *
 * @return the string without the emojis that were removed
 */
fun EmojiManager.removeAllEmojisExcept(
    str: String,
    emojisToKeep: Collection<AbstractEmoji>,
): String {
    val emojiTransformer =
        object : EmojiTransformer {
            override fun invoke(unicodeCandidate: UnicodeCandidate): String {
                return if (emojisToKeep.contains(unicodeCandidate.emoji)) {
                    unicodeCandidate.emoji?.unicode + unicodeCandidate.fitzpatrickUnicode
                } else {
                    ""
                }
            }
        }

    return parseFromUnicode(str, emojiTransformer)
}

/**
 * Detects all unicode emojis in input string and replaces them with the
 * return value of transformer.transform()
 *
 * @param input the string to process
 * @param transformer emoji transformer to apply to each emoji
 *
 * @return input string with all emojis transformed
 */
fun EmojiManager.parseFromUnicode(
    input: String,
    transformer: EmojiTransformer,
): String {
    var prev = 0
    val sb = StringBuilder(input.length)
    val replacements = unicodeCandidates(input)
    for (candidate in replacements) {
        sb.append(input.substring(prev, candidate.emojiStartIndex))

        sb.append(transformer(candidate))
        prev = candidate.fitzpatrickEndIndex
    }

    return sb.append(input.substring(prev)).toString()
}

fun EmojiManager.extractEmojis(input: String): List<String> {
    return unicodeCandidates(input)
        .mapNotNull { unicodeCandidate ->
            val emoji = unicodeCandidate.emoji
            if (emoji?.supportsFitzpatrick == true && unicodeCandidate.hasFitzpatrick()) {
                emoji.getUnicode(unicodeCandidate.fitzpatrick)
            } else {
                emoji?.unicode
            }
        }
}

/**
 * Generates a list UnicodeCandidates found in input string. A
 * UnicodeCandidate is created for every unicode emoticon found in input
 * string, additionally if Fitzpatrick modifier follows the emoji, it is
 * included in UnicodeCandidate. Finally, it contains start and end index of
 * unicode emoji itself **(WITHOUT Fitzpatrick modifier whether it is there or
 * not!)**.
 *
 * @param input String to find all unicode emojis in
 * @return List of UnicodeCandidates for each unicode emote in text
 */
internal fun EmojiManager.unicodeCandidates(input: String): List<UnicodeCandidate> {
    val inputCharArray = input.toCharArray()
    val candidates = ArrayList<UnicodeCandidate>()
    var next: UnicodeCandidate?
    var i = 0
    do {
        next =
            nextUnicodeCandidate(inputCharArray, i)?.apply {
                candidates.add(this)
                i = fitzpatrickEndIndex
            }
    } while (next != null)

    return candidates
}

/**
 * Finds the next UnicodeCandidate after a given starting index
 *
 * @param chars char array to find UnicodeCandidate in
 * @param start starting index for search
 * @return the next UnicodeCandidate or null if no UnicodeCandidate is found after start index
 */
internal fun EmojiManager.nextUnicodeCandidate(
    chars: CharArray,
    start: Int,
): UnicodeCandidate? {
    for (index in start until chars.size) {
        val emojiEnd = emojiEndPosition(chars, index)

        if (emojiEnd != -1) {
            val emoji = getByUnicode(String(chars, index, emojiEnd - index))
            val fitzpatrickString =
                if (emojiEnd + 2 <= chars.size) {
                    String(chars, emojiEnd, 2)
                } else {
                    null
                }
            return UnicodeCandidate(emoji, fitzpatrickString, index)
        }
    }

    return null
}

/**
 * Returns end index of a unicode emoji if it is found in text starting at
 * index startPos, -1 if not found.
 * This returns the longest matching emoji, for example, in
 * `"\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66"`
 * it will find `alias:family_man_woman_bo`y, **NOT** `alias:man`
 *
 * @param text the current text where we are looking for an emoji
 * @param startPos the position in the text where we should start looking for
 * an emoji end
 *
 * @return the end index of the unicode emoji starting at startPos. -1 if not found
 */
internal fun EmojiManager.emojiEndPosition(
    text: CharArray,
    startPos: Int,
): Int {
    var best = -1
    for (j in startPos + 1..text.size) {
        val status = isEmoji(text.copyOfRange(startPos, j))

        if (status.exactMatch()) {
            best = j
        } else if (status.impossibleMatch()) {
            return best
        }
    }

    return best
}
