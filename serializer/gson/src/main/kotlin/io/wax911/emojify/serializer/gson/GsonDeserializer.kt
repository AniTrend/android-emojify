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

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.wax911.emojify.contract.model.IEmoji
import io.wax911.emojify.contract.serializer.IEmojiDeserializer
import java.io.InputStream

/**
 * Default implementation for gson
 */
class GsonDeserializer : IEmojiDeserializer {
    private val gson = Gson()

    override fun decodeFromStream(inputStream: InputStream): List<IEmoji> {
        val myType = TypeToken.getParameterized(List::class.java, GsonEmoji::class.java).type
        return gson.fromJson(inputStream.reader(), myType)
    }
}
