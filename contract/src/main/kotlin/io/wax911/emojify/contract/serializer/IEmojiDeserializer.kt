package io.wax911.emojify.contract.serializer

import io.wax911.emojify.contract.model.AbstractEmoji
import java.io.InputStream

/**
 * Interface to implement for custom deserializer.
 */
interface IEmojiDeserializer {
    /**
     * Decodes the given [InputStream] to an object of type List<[AbstractEmoji]>
     */
    fun decodeFromStream(inputStream: InputStream): List<AbstractEmoji>
}
