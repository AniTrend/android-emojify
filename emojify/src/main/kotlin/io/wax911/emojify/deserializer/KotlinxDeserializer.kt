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

package io.wax911.emojify.deserializer

import io.wax911.emojify.initializer.IEmojiDeserializer
import io.wax911.emojify.model.AbstractEmoji
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

/**
 * Default implementation for kotlinx-serialization
 */
class KotlinxDeserializer : IEmojiDeserializer {
    private val json = Json { isLenient = true }
    @Serializable
    data class KotlinXEmoji(
        @SerialName("aliases") override val aliases: List<String>? = null,
        @SerialName("description") override val description: String? = null,
        @SerialName("emoji") override val emoji: String,
        @SerialName("emojiChar") override val emojiChar: String,
        @SerialName("supports_fitzpatrick") override val supportsFitzpatrick: Boolean = false,
        @SerialName("supports_gender") override val supportsGender: Boolean = false,
        @SerialName("tags") override val tags: List<String>? = null,
    ): AbstractEmoji(
        aliases = aliases,
        description = description,
        emoji = emoji,
        emojiChar = emojiChar,
        supportsFitzpatrick = supportsFitzpatrick,
        supportsGender = supportsGender,
        tags = tags
    )

    override fun decodeFromStream(inputStream: InputStream): List<AbstractEmoji> {
        val deserializer = ListSerializer(KotlinXEmoji.serializer())
        return json.decodeFromStream(deserializer, inputStream)
    }
}
