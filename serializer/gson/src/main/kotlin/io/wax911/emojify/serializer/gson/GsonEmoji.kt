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

package io.wax911.emojify.serializer.gson

import com.google.gson.annotations.SerializedName
import io.wax911.emojify.contract.model.AbstractEmoji

/**
 * Default implementation of AbstractEmoji for gson
 */
data class GsonEmoji(
    @SerializedName(value = "aliases") override val aliases: List<String>?,
    @SerializedName(value = "description") override val description: String?,
    @SerializedName(value = "emoji") override val emoji: String,
    @SerializedName(value = "emojiChar") override val emojiChar: String,
    @SerializedName(value = "supports_fitzpatrick") override val supportsFitzpatrick: Boolean,
    @SerializedName(value = "supports_gender") override val supportsGender: Boolean,
    @SerializedName(value = "tags") override val tags: List<String>?,
) : AbstractEmoji()
