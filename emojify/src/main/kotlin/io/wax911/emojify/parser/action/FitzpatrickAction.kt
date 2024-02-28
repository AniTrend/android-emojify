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

package io.wax911.emojify.parser.action

import io.wax911.emojify.util.Fitzpatrick
/**
 * Used to indicate what should be done when a Fitzpatrick modifier is found.
 */
sealed class FitzpatrickAction {
    /** Tries to match the Fitzpatrick modifier with the previous emoji */
    data object PARSE : FitzpatrickAction()

    /** Removes the Fitzpatrick modifier from the string */
    data object REMOVE : FitzpatrickAction()

    /** Ignores the [Fitzpatrick] modifier (it will stay in the string) */
    data object IGNORE : FitzpatrickAction()
}
