package io.wax911.emojify.contract.serializer

import io.wax911.emojify.contract.model.AbstractEmoji
import java.io.InputStream

interface IEmojiDeserializer {
    fun decodeFromStream(inputStream: InputStream): List<AbstractEmoji>
}
