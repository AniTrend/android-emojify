/*
 * Copyright 2026 AniTrend
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

package io.wax911.emojifysample

import android.app.Application
import androidx.startup.AppInitializer
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.serializer.kotlinx.KotlinxDeserializer

/**
 * Created by max on 2017/09/22.
 */
class App : Application() {
    /**
     * Application scope bound emojiManager, you could keep a reference to this object in a
     * dependency injector framework like as a singleton in `Hilt`, `Dagger` or `Koin`
     */
    internal val emojiManager: EmojiManager by lazy {
        EmojiManager.create(this, KotlinxDeserializer())
    }

    /**
     * Application scope bound emojiManager, you could keep a reference to this object in a
     * dependency injector framework like as a singleton in `Hilt`, `Dagger` or `Koin`
     */
    internal val startupEmojiManager: EmojiManager by lazy {
        // should already be initialized if we haven't disabled initialization in manifest
        // see: https://developer.android.com/topic/libraries/app-startup#disable-individual
        AppInitializer.getInstance(this)
            .initializeComponent(EmojiInitializer::class.java)
    }
}
