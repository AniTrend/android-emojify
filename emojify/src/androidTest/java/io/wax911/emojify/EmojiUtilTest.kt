package io.wax911.emojify

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.gson.GsonBuilder
import io.wax911.emojify.parser.EmojiParser
import org.junit.Assert.*
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

    @Test
    fun testApplicationContext() {
        assertNotNull(context)
    }

    @Test
    fun testEmojiLoading() {
        assertNotNull(EmojiManager.getAll())
    }

    @Test
    fun testEmojiByUnicode() {
        val emoji = EmojiManager.getByUnicode("\uD83D\uDC2D")
        assertNotNull(emoji)
        assertTrue(emoji?.htmlHex.equals("&#x1f42d;"))
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
        
        assertTrue(emoji!!.emoji == "🐭")

    }

    @Test
    fun testEmojiByDecimalHtml() {
        // get by decimal html
        val unicode = EmojiParser.parseToUnicode("&#128045;")
        assertNotNull(unicode)

        val emoji = EmojiManager.getByUnicode(unicode)
        assertNotNull(emoji)

        assertTrue(emoji!!.emoji == "🐭")

    }

    @Test
    fun testIsEmoji() {
        assertTrue(EmojiManager.isEmoji("&#128045;"))

        assertFalse(EmojiManager.isEmoji("&#123;"))

        assertTrue(EmojiManager.isEmoji("🐭"))

        assertTrue(EmojiManager.isEmoji("smile"))

        assertTrue(EmojiManager.isEmoji(":smiley:"))
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
        assertTrue(EmojiParser.extractEmojis(text).size == 8)
    }

    @Test
    fun testHtmlify() {
        val text = "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        val htmlifiedText = EmojiParser.parseToHtmlHexadecimal(text)

        assertEquals("A &#128049;, &#128054; and a &#128045; became friends. For &#128054;'s birthday party, they all had &#127828;s, &#127839;s, &#127850;s and &#127856;.", htmlifiedText)

        // also verify by emojifying htmlified text

        assertEquals("A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.", EmojiParser.parseToUnicode(htmlifiedText!!))
    }

    @Test
    fun testHexHtmlify() {
        val text = "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        val htmlifiedText = EmojiParser.parseToHtmlHexadecimal(text)

        assertEquals("A &#x1f431;, &#x1f436; and a &#x1f42d; became friends. For &#x1f436;'s birthday party, they all had &#x1f354;s, &#x1f35f;s, &#x1f36a;s and &#x1f370;.", htmlifiedText)
        // also verify by emojifying htmlified text
        assertEquals("A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.", EmojiParser.parseToUnicode(htmlifiedText!!))
    }

    @Test
    fun testShortCodifyFromEmojis() {
        val text = "A 🐱, 🐶 and a 🐭 became friends❤️. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰."
        assertEquals("A :cat:, :dog: and a :mouse: became friends:heart:. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.", EmojiParser.parseToAliases(text))

    }

    @Test
    fun testShortCodifyFromHtmlEntities() {
        var text = "A &#128049;, &#128054; and a &#128045; became friends. For &#128054;'s birthday party, they all had &#127828;s, &#127839;s, &#127850;s and &#127856;."
        assertEquals("A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.", EmojiParser.parseToAliases(text))

        text = "A &#x1f431;, &#x1f436; and a &#x1f42d; became friends. For &#x1f436;'s birthday party, they all had &#x1f354;s, &#x1f35f;s, &#x1f36a;s and &#x1f370;."
        assertEquals("A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.", EmojiParser.parseToAliases(text))

    }

    @Test
    fun surrogateDecimalToEmojiTest() {
        var emojiText = "A &#55357;&#56369;, &#x1f436;&#55357;&#56369; and a &#55357;&#56365; became friends. They had &#junk;&#55356;&#57172;&#junk;"
        assertEquals("A 🐱, 🐶🐱 and a 🐭 became friends. They had &#junk;🍔&#junk;", EmojiParser.parseToUnicode(emojiText))

        emojiText = "&#10084;&#65039;&#junk;&#55357;&#56374;"
        assertEquals("❤️&#junk;🐶", EmojiParser.parseToAliases(emojiText))

        emojiText = "&#55357;&#56833;"
        assertEquals("😁", EmojiParser.parseToAliases(emojiText))
    }

    @Test
    fun toSurrogateDecimalAndBackTest() {
        val text = "😃😃😅😃😶😝😗😗❤️😛😛😅❤️😛"
        val htmlifiedText = EmojiParser.parseToHtmlDecimal(text)
        assertEquals("&#55357;&#56835;&#55357;&#56835;&#55357;&#56837;&#55357;&#56835;&#55357;&#56886;&#55357;&#56861;&#55357;&#56855;&#55357;&#56855;&#55242;&#56164;&#55296;&#55823;&#55357;&#56859;&#55357;&#56859;&#55357;&#56837;&#55242;&#56164;&#55296;&#55823;&#55357;&#56859;", htmlifiedText)

        assertEquals(text, EmojiParser.parseToUnicode(htmlifiedText))
    }

    @Test
    fun surrogateToHTMLTest() {
        val surrogateText = "&#55357;&#56835;&#55357;&#56835;&#55357;&#56837;&#55357;&#56835;&#55357;&#56886;&#55357;&#56861;&#55357;&#56855;&#55357;&#56855;&#55242;&#56164;&#55296;&#55823;&#55357;&#56859;&#55357;&#56859;&#55357;&#56837;&#55242;&#56164;&#55296;&#55823;&#55357;&#56859;"

        val emojiText = "😃😃😅😃😶😝😗😗❤️😛😛😅❤️😛"
        val decHtmlString = EmojiParser.parseToHtmlDecimal(surrogateText)

        val hexHtmlString = EmojiParser.parseToHtmlHexadecimal(surrogateText)

        assertEquals("&#128515;&#128515;&#128517;&#128515;&#128566;&#128541;&#128535;&#128535;&#10084;&#65039;&#128539;&#128539;&#128517;&#10084;&#65039;&#128539;", decHtmlString)

        assertEquals("&#x1f603;&#x1f603;&#x1f605;&#x1f603;&#x1f636;&#x1f61d;&#x1f617;&#x1f617;&#x2764;&#xfe0f;&#x1f61b;&#x1f61b;&#x1f605;&#x2764;&#xfe0f;&#x1f61b;", hexHtmlString)

        assertEquals(emojiText, EmojiParser.parseToUnicode(decHtmlString))

        assertEquals(emojiText, EmojiParser.parseToUnicode(hexHtmlString!!))
    }
}