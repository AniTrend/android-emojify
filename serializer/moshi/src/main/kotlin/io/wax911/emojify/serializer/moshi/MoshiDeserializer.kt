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

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.wax911.emojify.contract.model.IEmoji
import io.wax911.emojify.contract.serializer.IEmojiDeserializer
import okio.buffer
import okio.source
import java.io.InputStream

/**
 * Default implementation for moshi
 */
class MoshiDeserializer : IEmojiDeserializer {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    override fun decodeFromStream(inputStream: InputStream): List<IEmoji> {
        val myType = Types.newParameterizedType(List::class.java, MoshiEmoji::class.java)
        return moshi.adapter<List<MoshiEmoji>>(myType).fromJson(inputStream.source().buffer()) ?: listOf()
    }
}
