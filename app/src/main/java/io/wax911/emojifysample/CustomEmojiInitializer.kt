package io.wax911.emojifysample

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.wax911.emojify.contract.model.AbstractEmoji
import io.wax911.emojify.contract.serializer.IEmojiDeserializer
import io.wax911.emojify.initializer.AbstractEmojiInitializer
import okio.buffer
import okio.source
import java.io.InputStream

class CustomEmojiInitializer: AbstractEmojiInitializer() {
    class MoshiDeserializer: IEmojiDeserializer {
        private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        @JsonClass(generateAdapter = true)
        data class MoshiEmoji(
            @Json(name = "aliases") override val aliases: List<String>? = null,
            @Json(name = "description") override val description: String? = null,
            @Json(name = "emoji") override val emoji: String,
            @Json(name = "emojiChar") override val emojiChar: String,
            @Json(name = "supports_fitzpatrick") override val supportsFitzpatrick: Boolean = false,
            @Json(name = "supports_gender") override val supportsGender: Boolean = false,
            @Json(name = "tags") override val tags: List<String>? = null,
        ): AbstractEmoji()

        override fun decodeFromStream(inputStream: InputStream): List<AbstractEmoji> {
            val myType = Types.newParameterizedType(List::class.java, MoshiEmoji::class.java)
            return moshi.adapter<List<MoshiEmoji>>(myType).fromJson(inputStream.source().buffer()) ?: listOf()
        }
    }

    class JacksonDeserializer: IEmojiDeserializer {
        private val jackson = ObjectMapper()
        data class JacksonEmoji(
            @JsonProperty(value = "aliases") override val aliases: List<String>?,
            @JsonProperty(value = "description") override val description: String?,
            @JsonProperty(value = "emoji") override val emoji: String,
            @JsonProperty(value = "emojiChar") override val emojiChar: String,
            @JsonProperty(value = "supports_fitzpatrick") override val supportsFitzpatrick: Boolean,
            @JsonProperty(value = "supports_gender") override val supportsGender: Boolean,
            @JsonProperty(value = "tags") override val tags: List<String>?,
        ): AbstractEmoji()

        override fun decodeFromStream(inputStream: InputStream): List<AbstractEmoji> {
            val myType = jackson.typeFactory.constructCollectionType(List::class.java, JacksonEmoji::class.java)
            return jackson.readValue(inputStream, myType)
        }
    }

    class GsonDeserializer: IEmojiDeserializer {
        private val gson = Gson()
        data class GsonEmoji(
            @SerializedName(value = "aliases") override val aliases: List<String>?,
            @SerializedName(value = "description") override val description: String?,
            @SerializedName(value = "emoji") override val emoji: String,
            @SerializedName(value = "emojiChar") override val emojiChar: String,
            @SerializedName(value = "supports_fitzpatrick") override val supportsFitzpatrick: Boolean,
            @SerializedName(value = "supports_gender") override val supportsGender: Boolean,
            @SerializedName(value = "tags") override val tags: List<String>?,
        ): AbstractEmoji()

        override fun decodeFromStream(inputStream: InputStream): List<AbstractEmoji> {
            val myType = TypeToken.getParameterized(List::class.java, GsonEmoji::class.java).type
            return gson.fromJson(inputStream.reader(), myType)
        }
    }

    override val serializer: IEmojiDeserializer = MoshiDeserializer()
}
