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

package io.wax911.emojify.contract.util.trie

/**
 * Emoji matching state representation
 */
sealed class Matches {
    /**
     * if char sequence in its entirety is an emoji
     */
    data object EXACTLY : Matches()

    /**
     * if char sequence matches prefix of an emoji
     */
    data object POSSIBLY : Matches()

    /**
     * if char sequence matches no emoji or prefix of an emoji
     */
    data object IMPOSSIBLE : Matches()

    /**
     * @return whether the current status is [EXACTLY]
     */
    fun exactMatch() = this is EXACTLY

    /**
     * @return whether the current status is [IMPOSSIBLE]
     */
    fun impossibleMatch() = this is IMPOSSIBLE
}
