package io.wax911.emojify

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.gson.GsonBuilder
import io.wax911.emojify.parser.EmojiParser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class EmojiUtilTest {

    private val context by lazy {
        InstrumentationRegistry.getTargetContext()
    }

    private val emojis by lazy {
        EmojiManager.initEmojiData(context)
    }

    @Before
    fun testApplicationContext() {
        assertNotNull(context)
    }

    @Before
    fun testEmojiLoading() {
        assertNotNull(emojis)
        assertNotNull(EmojiManager.getAll())
    }

    @Test
    fun testEmojiByUnicode() {
        val emoji = EmojiManager.getByUnicode("\uD83D\uDC2D")
        assertNotNull(emoji)
        assertEquals("&#x1f42d;", emoji?.htmlHex)
    }

    @Test
    fun testEmojiByShortCode() {
        // existing emoji
        var emoji = EmojiManager.getForAlias("blue_car")
        assertNotNull(emoji)
        assertEquals("🚙", emoji!!.emoji)

        // not an emoji character
        emoji = EmojiManager.getForAlias("bluecar")
        assertNull(emoji)
    }

    @Test
    fun testEmojiByShortCodeWithColons() {
        // existing emoji
        var emoji = EmojiManager.getForAlias(":blue_car:")
        assertNotNull(emoji)
        assertEquals("🚙", emoji!!.emoji)

        // not an emoji character
        emoji = EmojiManager.getForAlias(":bluecar:")
        assertNull(emoji)
    }

    @Test
    fun testEmojiByHexHtml() {
        // get by hexhtml
        val unicode = EmojiParser.parseToUnicode("&#x1f42d;")
        assertNotNull(unicode)
        
        val emoji = EmojiManager.getByUnicode(unicode)
        assertNotNull(emoji)

        assertEquals("🐭", emoji?.emoji)

    }

    @Test
    fun testEmojiByDecimalHtml() {
        // get by decimal html
        val unicode = EmojiParser.parseToUnicode("&#128045;")
        assertNotNull(unicode)

        val emoji = EmojiManager.getByUnicode(unicode)
        assertNotNull(emoji)

        assertEquals("🐭", emoji?.emoji)

    }

    @Test
    fun testIsEmoji() {
        assertFalse(EmojiManager.isEmoji("&#123;"))

        assertTrue(EmojiManager.isEmoji("🐭"))

        assertFalse(EmojiManager.isEmoji("smile"))

        assertTrue(EmojiManager.isEmoji(EmojiParser.parseToUnicode(":smiley:")))

        assertFalse(EmojiManager.isEmoji("&#128045;"))
    }

    @Test
    fun testEmojify1() {
        var text = "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        assertEquals("A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.", EmojiParser.parseToUnicode(text))

        text = "A :cat:, :dog:, :coyote: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        assertEquals("A 🐱, 🐶, :coyote: and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.", EmojiParser.parseToUnicode(text))

    }

    @Test
    fun testEmojify2() {
        var text = "A &#128049;, &#x1f436; and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        assertEquals("A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.", EmojiParser.parseToUnicode(text))

        text = "A &#128049;, &#x1f436;, &nbsp; and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        assertEquals("A 🐱, 🐶, &nbsp; and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.", EmojiParser.parseToUnicode(text))
    }

    @Test
    fun testCountEmojis() {
        val text = "A &#128049;, &#x1f436;,&nbsp;:coyote: and a :mouse: became friends. For :dog:'s birthday party, they all had 🍔s, :fries:s, :cookie:s and :cake:."

        val emojiText = EmojiParser.parseToUnicode(text)

        val emojiCount = EmojiParser.extractEmojis(emojiText).size

        assertEquals(8, emojiCount)
    }

    @Test
    fun testHtmlify() {
        val text = "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."

        val emojiText = EmojiParser.parseToUnicode(text)

        val htmlifiedText = EmojiParser.parseToHtmlDecimal(emojiText)

        assertEquals("A &#128049;, &#128054; and a &#128045; became friends. For &#128054;'s birthday party, they all had &#127828;s, &#127839;s, &#127850;s and &#127856;.", htmlifiedText)

        // also verify by emojifying htmlified text

        assertEquals("A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.", EmojiParser.parseToUnicode(htmlifiedText))
    }

    @Test
    fun testHexHtmlify() {
        val text = "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."

        val emojiText = EmojiParser.parseToUnicode(text)

        val htmlifiedText = EmojiParser.parseToHtmlHexadecimal(emojiText)

        assertNotNull(htmlifiedText)

        assertEquals("A &#x1f431;, &#x1f436; and a &#x1f42d; became friends. For &#x1f436;'s birthday party, they all had &#x1f354;s, &#x1f35f;s, &#x1f36a;s and &#x1f370;.", htmlifiedText)
        // also verify by emojifying htmlified text
        assertEquals("A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.", EmojiParser.parseToUnicode(htmlifiedText!!))
    }

    @Test
    fun testShortCodifyFromEmojis() {
        val text = "A 🐱, 🐶 and a 🐭 became friends❤️. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰."

        val expected = "A :cat:, :dog: and a :mouse: became friends:heart:️. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        val aliasText = EmojiParser.parseToAliases(text)

        assertEquals(expected, aliasText)
    }

    @Test
    fun testShortCodifyFromHtmlEntities() {
        var text = "A &#128049;, &#128054; and a &#128045; became friends. For &#128054;'s birthday party, they all had &#127828;s, &#127839;s, &#127850;s and &#127856;."

        var emojiText = EmojiParser.parseToUnicode(text)

        assertEquals("A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.", EmojiParser.parseToAliases(emojiText))

        text = "A &#x1f431;, &#x1f436; and a &#x1f42d; became friends. For &#x1f436;'s birthday party, they all had &#x1f354;s, &#x1f35f;s, &#x1f36a;s and &#x1f370;."

        emojiText = EmojiParser.parseToUnicode(text)

        assertEquals("A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.", EmojiParser.parseToAliases(emojiText))

    }

    @Test
    fun toSurrogateDecimalAndBackTest() {
        val text = "😃😃😅😃😶😝😗😗❤️😛😛😅❤️😛"
        val htmlifiedText = EmojiParser.parseToHtmlDecimal(text)
        assertEquals("&#128515;&#128515;&#128517;&#128515;&#128566;&#128541;&#128535;&#128535;&#10084;️&#128539;&#128539;&#128517;&#10084;️&#128539;", htmlifiedText)

        assertEquals(text, EmojiParser.parseToUnicode(htmlifiedText))
    }

    @Test
    fun surrogateToHTMLTest() {
        val emojiText = "😃😃😅😃😶😝😗😗❤️😛😛😅❤️😛"

        val decHtmlString = EmojiParser.parseToHtmlDecimal(emojiText)

        val hexHtmlString = EmojiParser.parseToHtmlHexadecimal(emojiText)

        assertNotNull(hexHtmlString)

        assertEquals("&#128515;&#128515;&#128517;&#128515;&#128566;&#128541;&#128535;&#128535;&#10084;️&#128539;&#128539;&#128517;&#10084;️&#128539;", decHtmlString)

        assertEquals("&#x1f603;&#x1f603;&#x1f605;&#x1f603;&#x1f636;&#x1f61d;&#x1f617;&#x1f617;&#x2764;️&#x1f61b;&#x1f61b;&#x1f605;&#x2764;️&#x1f61b;", hexHtmlString)

        assertEquals(emojiText, EmojiParser.parseToUnicode(decHtmlString))

        assertEquals(emojiText, EmojiParser.parseToUnicode(hexHtmlString!!))
    }
}