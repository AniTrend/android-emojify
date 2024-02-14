package io.wax911.emojify.initializer

import io.wax911.emojify.deserializer.KotlinxDeserializer

class EmojiInitializer: AEmojiInitializer() {
    /**
     * Kotlinx implementation is needed in your project for this to work
     */
    override val serializer: IEmojiDeserializer = KotlinxDeserializer()
}
