package io.wax911.emojify.initializer

import android.content.Context
import android.content.res.AssetManager
import androidx.startup.Initializer
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.model.Emoji
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class EmojiInitializer : Initializer<EmojiManager> {

    /**
     * Initializes emoji objects from an asset file in the library directory
     *
     * @param assetManager provide an assert manager
     * @param path location where emoji data can be found
     *
     * @throws IOException when the provided [assetManager] cannot open [path]
     * @throws SerializationException when an error occurs during deserialization
     */
    @Throws(IOException::class, SerializationException::class)
    fun initEmojiData(assetManager: AssetManager, path: String = DEFAULT_PATH): List<Emoji> {
        InputStreamReader(assetManager.open(path)).use { streamReader ->
            BufferedReader(streamReader).use { bufferedReader ->
                val json = Json { isLenient = true }
                val deserializer = ListSerializer(Emoji.serializer())
                return json.decodeFromString(deserializer, bufferedReader.readText())
            }
        }
    }

    /**
     * Initializes and a component given the application [Context]
     *
     * @param context The application context.
     */
    override fun create(context: Context): EmojiManager {
        val emojiManagerDefault = EmojiManager(emptyList())
        val result = runCatching {
            val emojis = initEmojiData(context.assets)
            EmojiManager(emojis)
        }.onFailure { it.printStackTrace() }
        return result.getOrNull() ?: emojiManagerDefault
    }

    /**
     * @return A list of dependencies that this [Initializer] depends on. This is
     * used to determine initialization order of [Initializer]s.
     *
     * For e.g. if a [Initializer] `B` defines another
     * [Initializer] `A` as its dependency, then `A` gets initialized before `B`.
     */
    override fun dependencies() = emptyList<Class<out Initializer<*>>>()


    companion object {
        /**
         * Default location with assets where emojis can be found
         */
        internal const val DEFAULT_PATH = "emoticons/emoji.json"
    }
}