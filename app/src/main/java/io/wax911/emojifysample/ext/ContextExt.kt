package io.wax911.emojifysample.ext

import androidx.fragment.app.FragmentActivity
import io.wax911.emojify.EmojiManager
import io.wax911.emojifysample.App

/**
 * Helper to get instance of emoji manager
 */
internal fun FragmentActivity.emojiManager(): EmojiManager {
    val app = applicationContext as App
    return app.emojiManager
}