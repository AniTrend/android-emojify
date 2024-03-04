package io.wax911.emojifysample

import io.wax911.emojify.contract.serializer.IEmojiDeserializer
import io.wax911.emojify.initializer.AbstractEmojiInitializer
import io.wax911.emojify.serializer.KotlinxDeserializer

class EmojiInitializer : AbstractEmojiInitializer() {
    override val serializer: IEmojiDeserializer = KotlinxDeserializer()
}
