package io.wax911.emojify.parser

import io.wax911.emojify.EmojiManager
import io.wax911.emojify.model.Emoji
import io.wax911.emojify.parser.candidate.AliasCandidate
import io.wax911.emojify.parser.candidate.UnicodeCandidate
import io.wax911.emojify.parser.common.EmojiTransformer
import io.wax911.emojify.parser.action.FitzpatrickAction
import java.util.*
import java.util.regex.Pattern

private val ALIAS_CANDIDATE_PATTERN = Pattern.compile(
        "(?<=:)\\+?(\\w|\\||\\-)+(?=:)"
)

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
        fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE
): String {
    val emojiTransformer = object : EmojiTransformer {
        override fun transform(unicodeCandidate: UnicodeCandidate): String {
            when (fitzpatrickAction) {
                FitzpatrickAction.PARSE -> {
                    return if (unicodeCandidate.hasFitzpatrick()) {
                        ":" +
                                unicodeCandidate.emoji?.aliases?.get(0) +
                                "|" +
                                unicodeCandidate.fitzpatrickType +
                                ":"
                    } else ":" +
                            unicodeCandidate.emoji?.aliases?.get(0) +
                            ":"
                }
                FitzpatrickAction.REMOVE -> return ":" + unicodeCandidate.emoji?.aliases?.get(0) + ":"
                FitzpatrickAction.IGNORE -> return ":" +
                        unicodeCandidate.emoji?.aliases?.get(0) +
                        ":" +
                        unicodeCandidate.fitzpatrickUnicode
                else -> {
                    return if (unicodeCandidate.hasFitzpatrick()) {
                        ":" + unicodeCandidate.emoji?.aliases?.get(0) + "|" + unicodeCandidate.fitzpatrickType + ":"
                    } else ":" + unicodeCandidate.emoji?.aliases?.get(0) + ":"
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
fun EmojiManager.replaceAllEmojis(str: String, replacementString: String): String {
    val emojiTransformer = object : EmojiTransformer {
        override fun transform(unicodeCandidate: UnicodeCandidate): String {
            return replacementString
        }
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
    // Get all the potential aliases
    val candidates = getAliasCandidates(input)

    // Replace the aliases by their unicode
    var result = input
    for (candidate in candidates) {
        getForAlias(candidate.alias)?.apply {
            if (supportsFitzpatrick || !supportsFitzpatrick && candidate.fitzpatrick == null) {
                var replacement = unicode
                if (candidate.fitzpatrick != null) {
                    replacement += candidate.fitzpatrick.unicode
                }
                result = result.replace(":" + candidate.fullString + ":", replacement)
            }
        }
    }

    // Replace the html
    for (emoji in emojiList) {
        result = result.replace(emoji.htmlHex, emoji.unicode)
        result = result.replace(emoji.htmlDec, emoji.unicode)
    }

    return result
}

internal fun getAliasCandidates(input: String): List<AliasCandidate> {
    val candidates = ArrayList<AliasCandidate>()

    var matcher = ALIAS_CANDIDATE_PATTERN.matcher(input)
    matcher = matcher.useTransparentBounds(true)
    while (matcher.find()) {
        val match = matcher.group()
        if (!match.contains("|")) {
            candidates.add(AliasCandidate(match, match, null))
        } else {
            val splits = match.split("\\|".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (splits.size == 2 || splits.size > 2) {
                candidates.add(AliasCandidate(match, splits.first(), splits[1]))
            } else {
                candidates.add(AliasCandidate(match, match, null))
            }
        }
    }
    return candidates
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
 * > `👦🏿` will be replaced by`&#128102;🏿`
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
        fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE
): String {
    val emojiTransformer = object : EmojiTransformer {
        override fun transform(unicodeCandidate: UnicodeCandidate): String? {
            return when (fitzpatrickAction) {
                FitzpatrickAction.PARSE,
                FitzpatrickAction.REMOVE ->
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
        fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE
): String? {
    val emojiTransformer = object : EmojiTransformer {
        override fun transform(unicodeCandidate: UnicodeCandidate): String? {
            return when (fitzpatrickAction) {
                FitzpatrickAction.PARSE,
                FitzpatrickAction.REMOVE ->
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
    val emojiTransformer = object : EmojiTransformer {
        override fun transform(unicodeCandidate: UnicodeCandidate): String {
            return ""
        }
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
fun EmojiManager.removeEmojis(str: String, emojisToRemove: Collection<Emoji>): String {
    val emojiTransformer = object : EmojiTransformer {
        override fun transform(unicodeCandidate: UnicodeCandidate): String {
            return if (!emojisToRemove.contains(unicodeCandidate.emoji)) {
                unicodeCandidate.emoji?.unicode + unicodeCandidate.fitzpatrickUnicode
            } else ""
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
fun EmojiManager.removeAllEmojisExcept(str: String, emojisToKeep: Collection<Emoji>): String {
    val emojiTransformer = object : EmojiTransformer {
        override fun transform(unicodeCandidate: UnicodeCandidate): String {
            return if (emojisToKeep.contains(unicodeCandidate.emoji)) {
                unicodeCandidate.emoji?.unicode + unicodeCandidate.fitzpatrickUnicode
            } else ""
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
fun EmojiManager.parseFromUnicode(input: String, transformer: EmojiTransformer): String {
    var prev = 0
    val sb = StringBuilder()
    val replacements = getUnicodeCandidates(input)
    for (candidate in replacements) {
        sb.append(input.substring(prev, candidate.emojiStartIndex))

        sb.append(transformer.transform(candidate))
        prev = candidate.fitzpatrickEndIndex
    }

    return sb.append(input.substring(prev)).toString()
}

fun EmojiManager.extractEmojis(input: String): List<String> {
    val emojis = getUnicodeCandidates(input)
    val result = ArrayList<String>()
    for (emoji in emojis) {
        emoji.emoji?.also {
            result.add(it.unicode)
        }
    }
    return result
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
internal fun EmojiManager.getUnicodeCandidates(input: String): List<UnicodeCandidate> {
    val inputCharArray = input.toCharArray()
    val candidates = ArrayList<UnicodeCandidate>()
    var next: UnicodeCandidate?
    var i = 0
    do {
        next = getNextUnicodeCandidate(inputCharArray, i)?.apply {
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
internal fun EmojiManager.getNextUnicodeCandidate(chars: CharArray, start: Int): UnicodeCandidate? {
    for (i in start until chars.size) {
        val emojiEnd = getEmojiEndPos(chars, i)

        if (emojiEnd != -1) {
            val emoji = getByUnicode(String(chars, i, emojiEnd - i))
            val fitzpatrickString = if (emojiEnd + 2 <= chars.size)
                String(chars, emojiEnd, 2)
            else
                null
            return UnicodeCandidate(emoji, fitzpatrickString, i)
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
internal fun EmojiManager.getEmojiEndPos(text: CharArray, startPos: Int): Int {
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
