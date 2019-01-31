package io.wax911.emojifysample

import android.app.Application
import io.wax911.emojify.EmojiManager

/**
 * Created by max on 2017/09/22.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        EmojiManager.initEmojiData(this)
    }
}
