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
import io.wax911.emojify.contract.serializer.IEmojiDeserializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

/**
 * Default implementation for kotlinx-serialization
 */
class KotlinxDeserializer : IEmojiDeserializer {
    private val json = Json { isLenient = true }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @Throws(SerializationException::class)
    override fun decodeFromStream(inputStream: InputStream): List<AbstractEmoji> {
        val deserializer = ListSerializer(KotlinxEmoji.serializer())
        return json.decodeFromStream(deserializer, inputStream)
    }
}
