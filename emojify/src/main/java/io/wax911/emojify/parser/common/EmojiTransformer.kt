package io.wax911.emojify.parser.common

import io.wax911.emojify.parser.candidate.UnicodeCandidate


interface EmojiTransformer {
    fun transform(unicodeCandidate: UnicodeCandidate): String?
}