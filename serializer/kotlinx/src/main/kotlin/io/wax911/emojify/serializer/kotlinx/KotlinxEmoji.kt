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

import io.wax911.emojify.contract.model.AbstractEmoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Default implementation of AbstractEmoji for kotlinx-serialization
 */
@Serializable
data class KotlinxEmoji(
    @SerialName("aliases") override val aliases: List<String>? = null,
    @SerialName("description") override val description: String? = null,
    @SerialName("emoji") override val emoji: String,
    @SerialName("emojiChar") override val emojiChar: String,
    @SerialName("supports_fitzpatrick") override val supportsFitzpatrick: Boolean = false,
    @SerialName("supports_gender") override val supportsGender: Boolean = false,
    @SerialName("tags") override val tags: List<String>? = null,
) : AbstractEmoji()
