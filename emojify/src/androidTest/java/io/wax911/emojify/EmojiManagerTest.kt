package io.wax911.emojify

import androidx.startup.AppInitializer
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import io.wax911.emojify.initializer.EmojiInitializer
import io.wax911.emojify.model.Emoji
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class EmojiManagerTest {

    private val context by lazy {
        InstrumentationRegistry.getInstrumentation().context
    }

    private val emojiManager by lazy {
        AppInitializer.getInstance(context)
            .initializeComponent(EmojiInitializer::class.java)
    }

    @Before
    fun testApplicationContext() {
        assertNotNull(context)
    }

    @Before
    fun testEmojiLoading() {
        assertNotNull(emojiManager)
        assertTrue(emojiManager.emojiList.isNotEmpty())
    }

    @Test
    fun getTrimmedAlias() {
        // GIVEN
        val alias = ":smile:"

        // WHEN
        val trimmed = emojiManager.trimAlias(alias)

        // THEN
        assertEquals("smile", trimmed)
    }

    @Test
    fun getForTag_with_unknown_tag_returns_null() {
        // GIVEN

        // WHEN
        val emojis = emojiManager.getForTag("jkahsgdfjksghfjkshf")

        // THEN
        assertNull(emojis)
    }

    @Test
    fun getForTag_returns_the_emojis_for_the_tag() {
        // GIVEN

        // WHEN
        val emojis = emojiManager.getForTag("happy")

        // THEN
        assertEquals(4, emojis!!.size)
        assertTrue(containsEmojis(
                emojis,
                "smile",
                "smiley",
                "grinning",
                "satisfied"
        ))
    }

    @Test
    fun getForAlias_with_unknown_alias_returns_null() {
        // GIVEN

        // WHEN
        val emoji = emojiManager.getForAlias("jkahsgdfjksghfjkshf")

        // THEN
        assertNull(emoji)
    }

    @Test
    fun getForAlias_returns_the_emoji_for_the_alias() {
        // GIVEN

        // WHEN
        val emoji = emojiManager.getForAlias("smile")

        // THEN
        assertEquals(
                "smiling face with open mouth and smiling eyes",
                emoji!!.description
        )
    }

    @Test
    fun getForAlias_with_colons_returns_the_emoji_for_the_alias() {
        // GIVEN

        // WHEN
        val emoji = emojiManager.getForAlias(":smile:")

        // THEN
        assertEquals(
                "smiling face with open mouth and smiling eyes",
                emoji!!.description
        )
    }

    @Test
    fun isEmoji_for_an_emoji_returns_true() {
        // GIVEN
        val emoji = "😀"

        // WHEN
        val isEmoji = emojiManager.isEmoji(emoji)

        // THEN
        assertTrue(isEmoji)
    }

    @Test
    fun isEmoji_with_fitzpatric_modifier_returns_true() {
        // GIVEN
        val emoji = "\uD83E\uDD30\uD83C\uDFFB"

        // WHEN
        val isEmoji = emojiManager.isEmoji(emoji)

        // THEN
        assertTrue(isEmoji)
    }

    @Test
    fun isEmoji_for_a_non_emoji_returns_false() {
        // GIVEN
        val str = "test"

        // WHEN
        val isEmoji = emojiManager.isEmoji(str)

        // THEN
        assertFalse(isEmoji)
    }

    @Test
    fun isEmoji_for_an_emoji_and_other_chars_returns_false() {
        // GIVEN
        val str = "😀 test"

        // WHEN
        val isEmoji = emojiManager.isEmoji(str)

        // THEN
        assertFalse(isEmoji)
    }

    @Test
    fun isOnlyEmojis_for_an_emoji_returns_true() {
        // GIVEN
        val str = "😀"

        // WHEN
        val isEmoji = emojiManager.isOnlyEmojis(str)

        // THEN
        assertTrue(isEmoji)
    }

    @Test
    fun isOnlyEmojis_for_emojis_returns_true() {
        // GIVEN
        val str = "😀😀😀"

        // WHEN
        val isEmoji = emojiManager.isOnlyEmojis(str)

        // THEN
        assertTrue(isEmoji)
    }

    @Test
    fun isOnlyEmojis_for_random_string_returns_false() {
        // GIVEN
        val str = "😀a"

        // WHEN
        val isEmoji = emojiManager.isOnlyEmojis(str)

        // THEN
        assertFalse(isEmoji)
    }

    @Test
    fun getAllTags_returns_the_tags() {
        // GIVEN

        // WHEN
        val tags = emojiManager.getAllTags()

        // THEN
        // We know the number of distinct tags int the...!
        assertEquals(656, tags.size)
    }

    @Test
    fun getAll_does_not_return_duplicates() {
        // GIVEN

        // WHEN
        val emojis = emojiManager.emojiList

        // THEN
        val unicodes = HashSet<String>()
        for (emoji in emojis) {
            assertFalse(
                    "Duplicate: " + emoji.description!!,
                    unicodes.contains(emoji.unicode)
            )
            unicodes.add(emoji.unicode)
        }
        assertEquals(unicodes.size, emojis.size)
    }

    @Test
    fun no_duplicate_alias() {
        // GIVEN

        // WHEN
        val emojis = emojiManager.emojiList

        // THEN
        val aliases = HashSet<String>()
        val duplicates = HashSet<String>()
        for (emoji in emojis) {
            for (alias in emoji.aliases!!) {
                if (aliases.contains(alias)) {
                    duplicates.add(alias)
                }
                aliases.add(alias)
            }
        }
        assertEquals("Duplicates: $duplicates", duplicates.size, 0)
    }

    companion object {

        fun containsEmojis(emojis: Iterable<Emoji>, vararg aliases: String): Boolean {
            for (alias in aliases) {
                val contains = containsEmoji(emojis, alias)
                if (!contains) {
                    return false
                }
            }
            return true
        }

        private fun containsEmoji(emojis: Iterable<Emoji>, alias: String): Boolean {
            for (emoji in emojis) {
                emoji.aliases?.forEach { al ->
                    if (alias == al)
                        return true
                }
            }
            return false
        }
    }
}
