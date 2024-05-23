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

package io.wax911.emojify.serializer.kotlinx

import io.wax911.emojify.contract.model.IEmoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Default implementation of IEmoji for kotlinx-serialization
 */
@Serializable
data class KotlinxEmoji(
    @SerialName(value = "aliases") override val aliases: List<String>? = null,
    @SerialName(value = "description") override val description: String? = null,
    @SerialName(value = "emoji") override val emoji: String,
    @SerialName(value = "emojiChar") override val emojiChar: String,
    @SerialName(value = "supports_fitzpatrick") override val supportsFitzpatrick: Boolean = false,
    @SerialName(value = "supports_gender") override val supportsGender: Boolean = false,
    @SerialName(value = "tags") override val tags: List<String>? = null,
    @SerialName(value = "unicode") override val unicode: String,
    @SerialName(value = "htmlDec") override val htmlDec: String,
    @SerialName(value = "htmlHex") override val htmlHex: String,
) : IEmoji
