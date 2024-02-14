package io.wax911.emojify.deserializer

import io.wax911.emojify.initializer.IEmojiDeserializer
import io.wax911.emojify.model.Emoji
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

class KotlinxDeserializer: IEmojiDeserializer {
    private val json = Json { isLenient = true }

    override fun decodeFromStream(inputStream: InputStream): List<Emoji> {
        val deserializer = ListSerializer(Emoji.serializer())
        return json.decodeFromStream(deserializer, inputStream)
    }
}
