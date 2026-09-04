package io.wax911.emojify.serializer.moshi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoshiDeserializerTest {

    @Test
    fun `check that entries are decoded through the generated adapter`() {
        val json =
            """
            [
              {
                "emoji": "😀",
                "description": "grinning face",
                "supportsFitzpatrick": false,
                "tags": ["happy", "smile"],
                "unicode": "U+1F600",
                "htmlDec": "&#128512;",
                "htmlHex": "&#x1f600;",
                "shortCodes": ["grinning"]
              }
            ]
            """.trimIndent()

        val result = MoshiDeserializer().decodeFromStream(json.byteInputStream())

        assertEquals(1, result.size)
        val emoji = result.first()
        assertEquals("😀", emoji.emoji)
        assertEquals("grinning face", emoji.description)
        assertEquals(listOf("grinning"), emoji.shortCodes)
        assertEquals(listOf("happy", "smile"), emoji.tags)
        assertEquals("&#128512;", emoji.htmlDec)
        assertEquals("&#x1f600;", emoji.htmlHex)
    }

    @Test
    fun `check that omitted optional fields fall back to their defaults`() {
        // The bundled asset leaves supportsFitzpatrick and tags off most entries, so the adapter
        // has to honour the constructor defaults rather than fail or null them out.
        val json =
            """
            [
              {
                "emoji": "🔥",
                "description": "fire",
                "unicode": "U+1F525",
                "htmlDec": "&#128293;",
                "htmlHex": "&#x1f525;"
              }
            ]
            """.trimIndent()

        val emoji = MoshiDeserializer().decodeFromStream(json.byteInputStream()).first()

        assertEquals(false, emoji.supportsFitzpatrick)
        assertNull(emoji.shortCodes)
        assertNull(emoji.tags)
    }
}
