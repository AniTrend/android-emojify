package io.wax911.emojify.initializer

import io.wax911.emojify.model.Emoji
import java.io.InputStream

interface IEmojiDeserializer {
    /**
     * Decodes the given [InputStream] to an object of type List<[Emoji]>
     */
    fun decodeFromStream(inputStream: InputStream): List<Emoji>
}
