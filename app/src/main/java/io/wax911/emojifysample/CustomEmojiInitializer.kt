package io.wax911.emojifysample

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.wax911.emojify.initializer.AEmojiInitializer
import io.wax911.emojify.initializer.IEmojiDeserializer
import io.wax911.emojify.model.Emoji
import okio.buffer
import okio.source
import java.io.InputStream

class CustomEmojiInitializer: AEmojiInitializer() {
    class MoshiDeserializer: IEmojiDeserializer {
        private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

        override fun decodeFromStream(inputStream: InputStream): List<Emoji> {
            val myType = Types.newParameterizedType(List::class.java, Emoji::class.java)
            return moshi.adapter<List<Emoji>>(myType).fromJson(inputStream.source().buffer()) ?: listOf()
        }
    }

    class JacksonDeserializer: IEmojiDeserializer {
        private val jackson = ObjectMapper()

        override fun decodeFromStream(inputStream: InputStream): List<Emoji> {
            val myType = jackson.typeFactory.constructCollectionType(List::class.java, Emoji::class.java)
            return jackson.readValue(inputStream, myType)
        }
    }

    class GsonDeserializer: IEmojiDeserializer {
        private val gson = Gson()

        override fun decodeFromStream(inputStream: InputStream): List<Emoji> {
            val myType = TypeToken.getParameterized(List::class.java, Emoji::class.java).type
            return gson.fromJson(inputStream.reader(), myType)
        }
    }

    override val serializer: IEmojiDeserializer = MoshiDeserializer()
}
