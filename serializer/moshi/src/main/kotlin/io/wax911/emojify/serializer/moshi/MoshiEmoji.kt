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

package io.wax911.emojify.serializer.moshi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.wax911.emojify.contract.model.IEmoji

/**
 * Default implementation of IEmoji for moshi
 */
@JsonClass(generateAdapter = true)
data class MoshiEmoji(
    @Json(name = "aliases") override val aliases: List<String>? = null,
    @Json(name = "description") override val description: String? = null,
    @Json(name = "emoji") override val emoji: String,
    @Json(name = "emojiChar") override val emojiChar: String,
    @Json(name = "supports_fitzpatrick") override val supportsFitzpatrick: Boolean = false,
    @Json(name = "supports_gender") override val supportsGender: Boolean = false,
    @Json(name = "tags") override val tags: List<String>? = null,
    @Json(name = "unicode") override val unicode: String,
    @Json(name = "htmlDec") override val htmlDec: String,
    @Json(name = "htmlHex") override val htmlHex: String,
) : IEmoji
