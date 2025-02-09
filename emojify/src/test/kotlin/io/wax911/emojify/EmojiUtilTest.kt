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
import io.wax911.emojify.parser.parseToHtmlDecimal
import io.wax911.emojify.parser.parseToHtmlHexadecimal
import io.wax911.emojify.parser.parseToUnicode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EmojiUtilTest : EmojiLoader() {

    @Test
    fun testEmojiByUnicode() {
        val emoji = emojiManager.getByUnicode("\uD83D\uDC2D")
        assertNotNull(emoji)
        assertEquals("&#x1f42d;", emoji?.htmlHex)
    }

    @Test
    fun testEmojiByHexHtml() {
        // get by hexhtml
        val unicode = emojiManager.parseToUnicode("&#x1f42d;")
        assertNotNull(unicode)

        val emoji = emojiManager.getByUnicode(unicode)
        assertNotNull(emoji)

        assertEquals("🐭", emoji?.emoji)
    }

    @Test
    fun testEmojiByHexHtmlToUnicode() {
        // get by hexhtml
        val unicode = emojiManager.parseToUnicode("&#x1f42d; & a &#128104;&#8205;&#127979;&#x1f3ff;")
        assertNotNull(unicode)

        assertEquals("🐭 & a 👨‍🏫🏿", unicode)
    }

    @Test
    fun testEmojiByDecimalHtml() {
        // get by decimal html
        val unicode = emojiManager.parseToUnicode("&#128045;")
        assertNotNull(unicode)

        val emoji = emojiManager.getByUnicode(unicode)
        assertNotNull(emoji)

        assertEquals("🐭", emoji?.emoji)
    }

    @Test
    fun testIsEmoji() {
        assertFalse(emojiManager.isEmoji("&#123;"))

        assertTrue(emojiManager.isEmoji("🐭"))

        assertFalse(emojiManager.isEmoji("smile"))

        assertFalse(emojiManager.isEmoji("&#128045;"))
    }

    @Test
    fun testEmojify2() {
        var text =
            "A &#128049;, &#x1f436; and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        assertEquals(
            "A 🐱, 🐶 and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.",
            emojiManager.parseToUnicode(text),
        )

        text =
            "A &#128049;, &#x1f436;, &nbsp; and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:."
        assertEquals(
            "A 🐱, 🐶, &nbsp; and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.",
            emojiManager.parseToUnicode(text),
        )
    }

    @Test
    fun testCountEmojis() {
        val text =
            "A &#128049;, &#x1f436;,&nbsp;:coyote: and a :mouse: became friends. For :dog:'s birthday party, they all had 🍔s, :fries:s, :cookie:s and :cake:."

        val emojiText = emojiManager.parseToUnicode(text)

        val emojiCount = emojiManager.extractEmojis(emojiText).size

        assertEquals(3, emojiCount)
    }

    @Test
    fun testHtmlify() {
        val text = "A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰."

        val emojiText = emojiManager.parseToUnicode(text)

        val htmlifiedText = emojiManager.parseToHtmlDecimal(emojiText)

        assertEquals(
            "A &#128049;, &#128054; and a &#128045; became friends. For &#128054;'s birthday party, they all had &#127828;s, &#127839;s, &#127850;s and &#127856;.",
            htmlifiedText,
        )

        // also verify by emojifying htmlified text

        assertEquals(
            "A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.",
            emojiManager.parseToUnicode(htmlifiedText),
        )
    }

    @Test
    fun testHexHtmlify() {
        val text = "A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰."

        val emojiText = emojiManager.parseToUnicode(text)

        val htmlifiedText = emojiManager.parseToHtmlHexadecimal(emojiText)

        assertNotNull(htmlifiedText)

        assertEquals(
            "A &#x1f431;, &#x1f436; and a &#x1f42d; became friends. For &#x1f436;'s birthday party, they all had &#x1f354;s, &#x1f35f;s, &#x1f36a;s and &#x1f370;.",
            htmlifiedText,
        )
        // also verify by emojifying htmlified text
        assertEquals(
            "A 🐱, 🐶 and a 🐭 became friends. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.",
            emojiManager.parseToUnicode(htmlifiedText),
        )
    }

    @Test
    fun toSurrogateDecimalAndBackTest() {
        val text = "😃😃😅😃😶😝😗😗❤️😛😛😅❤️😛"
        val htmlifiedText = emojiManager.parseToHtmlDecimal(text)
        assertEquals(
            "&#128515;&#128515;&#128517;&#128515;&#128566;&#128541;&#128535;&#128535;&#10084;&#65039;&#128539;&#128539;&#128517;&#10084;&#65039;&#128539;",
            htmlifiedText,
        )

        assertEquals(text, emojiManager.parseToUnicode(htmlifiedText))
    }

    @Test
    fun surrogateToHTMLTest() {
        val emojiText = "😃😃😅😃😶😝😗😗❤️😛😛😅❤️😛"

        val decHtmlString = emojiManager.parseToHtmlDecimal(emojiText)

        val hexHtmlString = emojiManager.parseToHtmlHexadecimal(emojiText)

        assertNotNull(hexHtmlString)

        assertEquals(
            "&#128515;&#128515;&#128517;&#128515;&#128566;&#128541;&#128535;&#128535;&#10084;&#65039;&#128539;&#128539;&#128517;&#10084;&#65039;&#128539;",
            decHtmlString,
        )

        assertEquals(
            "&#x1f603;&#x1f603;&#x1f605;&#x1f603;&#x1f636;&#x1f61d;&#x1f617;&#x1f617;&#x2764;&#xfe0f;&#x1f61b;&#x1f61b;&#x1f605;&#x2764;&#xfe0f;&#x1f61b;",
            hexHtmlString,
        )

        assertEquals(emojiText, emojiManager.parseToUnicode(decHtmlString))

        assertEquals(emojiText, emojiManager.parseToUnicode(hexHtmlString))
    }
}
