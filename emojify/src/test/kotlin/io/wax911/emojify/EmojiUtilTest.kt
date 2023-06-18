/*
 * Copyright 2023 AniTrend
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

package io.wax911.emojify

import io.wax911.emojify.core.EmojiLoader
import io.wax911.emojify.parser.extractEmojis
import io.wax911.emojify.parser.parseToAliases
import io.wax911.emojify.parser.parseToHtmlDecimal
import io.wax911.emojify.parser.parseToHtmlHexadecimal
import io.wax911.emojify.parser.parseToUnicode
import org.junit.Assert
import org.junit.Test

class EmojiUtilTest : EmojiLoader() {

    @Test
    fun testEmojiByUnicode() {
        val emoji = emojiManager.getByUnicode("\uD83D\uDC2D")
        Assert.assertNotNull(emoji)
        Assert.assertEquals("&#x1f42d;", emoji?.htmlHex)
    }

    @Test
    fun testEmojiByShortCode() {
        // existing emoji
        var emoji = emojiManager.getForAlias("blue_car")
        Assert.assertNotNull(emoji)
        Assert.assertEquals("🚙", emoji!!.emoji)

        // not an emoji character
        emoji = emojiManager.getForAlias("bluecar")
        Assert.assertNull(emoji)
    }

    @Test
    fun testEmojiByShortCodeWithColons() {
        // existing emoji
        var emoji = emojiManager.getForAlias(":blue_car:")
        Assert.assertNotNull(emoji)
        Assert.assertEquals("🚙", emoji!!.emoji)

        // not an emoji character
        emoji = emojiManager.getForAlias(":bluecar:")
        Assert.assertNull(emoji)
    }

    @Test
    fun testEmojiByHexHtml() {
        // get by hexhtml
        val unicode = emojiManager.parseToUnicode("&#x1f42d;")
        Assert.assertNotNull(unicode)

        val emoji = emojiManager.getByUnicode(unicode)
        Assert.assertNotNull(emoji)

        Assert.assertEquals("🐭", emoji?.emoji)
    }

    @Test
    fun testEmojiByDecimalHtml() {
        // get by decimal html
        val unicode = emojiManager.parseToUnicode("&#128045;")
        Assert.assertNotNull(unicode)

        val emoji = emojiManager.getByUnicode(unicode)
        Assert.assertNotNull(emoji)

        Assert.assertEquals("🐭", emoji?.emoji)
    }

    @Test
    fun testIsEmoji() {
        Assert.assertFalse(emojiManager.isEmoji("&#123;"))

        Assert.assertTrue(emojiManager.isEmoji("🐭"))

        Assert.assertFalse(emojiManager.isEmoji("smile"))

        Assert.assertTrue(emojiManager.isEmoji(emojiManager.parseToUnicode(":smiley:")))

        Assert.assertFalse(emojiManager.isEmoji("&#128045;"))
    }

    @Test
    fun testEmojify1() {
        var text = "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        Assert.assertEquals(
            "A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.",
            emojiManager.parseToUnicode(text),
        )

        text = "A :cat:, :dog:, :coyote: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        Assert.assertEquals(
            "A 🐱, 🐶, :coyote: and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.",
            emojiManager.parseToUnicode(text),
        )
    }

    @Test
    fun testEmojify2() {
        var text = "A &#128049;, &#x1f436; and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        Assert.assertEquals(
            "A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.",
            emojiManager.parseToUnicode(text),
        )

        text = "A &#128049;, &#x1f436;, &nbsp; and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        Assert.assertEquals(
            "A 🐱, 🐶, &nbsp; and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.",
            emojiManager.parseToUnicode(text),
        )
    }

    @Test
    fun testCountEmojis() {
        val text = "A &#128049;, &#x1f436;,&nbsp;:coyote: and a :mouse: became friends. For :dog:'s birthday party, they all had 🍔s, :fries:s, :cookie:s and :cake:."

        val emojiText = emojiManager.parseToUnicode(text)

        val emojiCount = emojiManager.extractEmojis(emojiText).size

        Assert.assertEquals(8, emojiCount)
    }

    @Test
    fun testHtmlify() {
        val text = "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."

        val emojiText = emojiManager.parseToUnicode(text)

        val htmlifiedText = emojiManager.parseToHtmlDecimal(emojiText)

        Assert.assertEquals(
            "A &#128049;, &#128054; and a &#128045; became friends. For &#128054;'s birthday party, they all had &#127828;s, &#127839;s, &#127850;s and &#127856;.",
            htmlifiedText,
        )

        // also verify by emojifying htmlified text

        Assert.assertEquals(
            "A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.",
            emojiManager.parseToUnicode(htmlifiedText),
        )
    }

    @Test
    fun testHexHtmlify() {
        val text = "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."

        val emojiText = emojiManager.parseToUnicode(text)

        val htmlifiedText = emojiManager.parseToHtmlHexadecimal(emojiText)

        Assert.assertNotNull(htmlifiedText)

        Assert.assertEquals(
            "A &#x1f431;, &#x1f436; and a &#x1f42d; became friends. For &#x1f436;'s birthday party, they all had &#x1f354;s, &#x1f35f;s, &#x1f36a;s and &#x1f370;.",
            htmlifiedText,
        )
        // also verify by emojifying htmlified text
        Assert.assertEquals(
            "A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.",
            emojiManager.parseToUnicode(htmlifiedText!!),
        )
    }

    @Test
    fun testShortCodifyFromEmojis() {
        val text = "A 🐱, 🐶 and a 🐭 became friends❤️. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰."

        val expected = "A :cat:, :dog: and a :mouse: became friends:heart:️. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        val aliasText = emojiManager.parseToAliases(text)

        Assert.assertEquals(expected, aliasText)
    }

    @Test
    fun testShortCodifyFromHtmlEntities() {
        var text = "A &#128049;, &#128054; and a &#128045; became friends. For &#128054;'s birthday party, they all had &#127828;s, &#127839;s, &#127850;s and &#127856;."

        var emojiText = emojiManager.parseToUnicode(text)

        Assert.assertEquals(
            "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.",
            emojiManager.parseToAliases(emojiText),
        )

        text = "A &#x1f431;, &#x1f436; and a &#x1f42d; became friends. For &#x1f436;'s birthday party, they all had &#x1f354;s, &#x1f35f;s, &#x1f36a;s and &#x1f370;."

        emojiText = emojiManager.parseToUnicode(text)

        Assert.assertEquals(
            "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.",
            emojiManager.parseToAliases(emojiText),
        )
    }

    @Test
    fun toSurrogateDecimalAndBackTest() {
        val text = "😃😃😅😃😶😝😗😗❤️😛😛😅❤️😛"
        val htmlifiedText = emojiManager.parseToHtmlDecimal(text)
        Assert.assertEquals(
            "&#128515;&#128515;&#128517;&#128515;&#128566;&#128541;&#128535;&#128535;&#10084;️&#128539;&#128539;&#128517;&#10084;️&#128539;",
            htmlifiedText,
        )

        Assert.assertEquals(text, emojiManager.parseToUnicode(htmlifiedText))
    }

    @Test
    fun surrogateToHTMLTest() {
        val emojiText = "😃😃😅😃😶😝😗😗❤️😛😛😅❤️😛"

        val decHtmlString = emojiManager.parseToHtmlDecimal(emojiText)

        val hexHtmlString = emojiManager.parseToHtmlHexadecimal(emojiText)

        Assert.assertNotNull(hexHtmlString)

        Assert.assertEquals(
            "&#128515;&#128515;&#128517;&#128515;&#128566;&#128541;&#128535;&#128535;&#10084;️&#128539;&#128539;&#128517;&#10084;️&#128539;",
            decHtmlString,
        )

        Assert.assertEquals(
            "&#x1f603;&#x1f603;&#x1f605;&#x1f603;&#x1f636;&#x1f61d;&#x1f617;&#x1f617;&#x2764;️&#x1f61b;&#x1f61b;&#x1f605;&#x2764;️&#x1f61b;",
            hexHtmlString,
        )

        Assert.assertEquals(emojiText, emojiManager.parseToUnicode(decHtmlString))

        Assert.assertEquals(emojiText, emojiManager.parseToUnicode(hexHtmlString!!))
    }
}
