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

package io.wax911.emojify.util

import io.wax911.emojify.contract.model.IEmoji

/**
 * Returns the unicode representation of the emoji associated with the
 * provided Fitzpatrick modifier.
 *
 * If the modifier is null, then the result is similar to [IEmoji.unicode]
 *
 * @param fitzpatrick the fitzpatrick modifier or null
 *
 * @return the unicode representation
 *
 * @throws UnsupportedOperationException if the emoji doesn't support the Fitzpatrick modifiers
 */
@Throws(UnsupportedOperationException::class)
fun IEmoji.getUnicode(fitzpatrick: Fitzpatrick?): String {
    if (!supportsFitzpatrick) {
        throw UnsupportedOperationException(
            """
            Cannot get the unicode with a fitzpatrick modifier,
            the emoji doesn't support fitzpatrick.
            """.trimIndent(),
        )
    } else if (fitzpatrick == null) {
        return unicode
    }
    return unicode + fitzpatrick.unicode
}
