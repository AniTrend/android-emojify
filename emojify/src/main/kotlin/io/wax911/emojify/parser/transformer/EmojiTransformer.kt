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

package io.wax911.emojify.parser.transformer

import io.wax911.emojify.parser.candidate.UnicodeCandidate

/**
 * Contract for replacing a Unicode emoji candidate during text conversion.
 */
interface EmojiTransformer {
    /**
     * Applies a transformation to one [UnicodeCandidate].
     *
     * @param unicodeCandidate the emoji match to transform
     * @return replacement text for the candidate; implementations must return
     *   a non-null value for every candidate, since the parser appends each
     *   result directly to the output
     */
    operator fun invoke(unicodeCandidate: UnicodeCandidate): String?
}
