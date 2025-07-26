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

/**
 * @property description the description of the emoji
 * @property emoji unicode emoji
 * @property supportsFitzpatrick true if the emoji supports the Fitzpatrick modifiers, else false
 * @property tags a list of tags for this emoji
 */
interface IEmoji {
    val emoji: String
    val description: String
    val supportsFitzpatrick: Boolean
    val tags: List<String>?
    val unicode: String
    val htmlDec: String
    val htmlHex: String
    val shortCodes: List<String>?
}
