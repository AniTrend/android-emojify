/*
 * Copyright 2023 AniTrend
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

package io.wax911.emojify.core

import io.wax911.emojify.EmojiManager
import io.wax911.emojify.model.Emoji
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

abstract class EmojiLoader {

    protected val emojiManager = EmojiLoader::class.java.getResourceAsStream("emoji.json")
        ?.use {
            val json = Json { isLenient = true }
            val deserializer = ListSerializer(Emoji.serializer())
            json.decodeFromStream(deserializer, it)
        }
        .orEmpty()
        .let(::EmojiManager)
}
