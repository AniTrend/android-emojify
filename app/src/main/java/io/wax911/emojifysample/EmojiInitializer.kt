package io.wax911.emojifysample

import android.content.Context
import androidx.startup.Initializer
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.contract.serializer.IEmojiDeserializer
import io.wax911.emojify.serializer.kotlinx.KotlinxDeserializer

class EmojiInitializer : Initializer<EmojiManager> {
    private val serializer: IEmojiDeserializer = KotlinxDeserializer()

    /**
     * Initializes and a component given the application [Context]
     *
     * @param context The application context.
     */
    override fun create(context: Context) = EmojiManager.create(context, serializer)

    /**
     * @return A list of dependencies that this [Initializer] depends on. This is
     * used to determine initialization order of [Initializer]s.
     *
     * For e.g. if a [Initializer] `B` defines another
     * [Initializer] `A` as its dependency, then `A` gets initialized before `B`.
     */
    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}
