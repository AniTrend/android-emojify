/*
 * Copyright 2021 AniTrend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.wax911.emojify.initializer

import android.content.Context
import androidx.startup.Initializer
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.contract.serializer.IEmojiDeserializer

/**
 * Abstract the logic of Initializer<EmojiManager> so that
 */
abstract class AbstractEmojiInitializer : Initializer<EmojiManager> {
    abstract val serializer: IEmojiDeserializer

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

    companion object {
        /**
         * Default location with assets where emojis can be found
         */
        internal const val DEFAULT_PATH = "emoticons/emoji.json"
    }
}
