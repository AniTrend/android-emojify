package io.wax911.emojify.parser.transformer

import io.wax911.emojify.parser.candidate.UnicodeCandidate

/**
 * Emoji transformer contract
 */
interface EmojiTransformer {
    /**
     * Given a [unicodeCandidate] applies a transformation
     *
     * @return Transformation result for the [unicodeCandidate]
     */
    fun transform(unicodeCandidate: UnicodeCandidate): String?
}