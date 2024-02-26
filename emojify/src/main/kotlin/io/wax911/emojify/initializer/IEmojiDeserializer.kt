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

package io.wax911.emojify.initializer

import io.wax911.emojify.model.AbstractEmoji
import java.io.InputStream

/**
 * Interface to implement if user wants to use a custom deserializer.
 * For more information on the necessary steps refer to README.md
 */
interface IEmojiDeserializer {
    /**
     * Decodes the given [InputStream] to an object of type List<[AbstractEmoji]>
     */
    fun decodeFromStream(inputStream: InputStream): List<AbstractEmoji>
}
