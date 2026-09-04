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

package io.wax911.emojify.serializer.moshi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Verifies that [MoshiDeserializer] decodes emoji entries through the KSP generated adapter.
 */
class MoshiDeserializerTest {

    /**
     * A fully populated entry should round-trip every [io.wax911.emojify.contract.model.IEmoji]
     * property, proving the generated adapter is in use rather than the reflective fallback.
     */
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

    /**
     * The bundled asset leaves `supportsFitzpatrick` and `tags` off most entries, so the generated
     * adapter has to honour the constructor defaults rather than fail or null them out.
     */
    @Test
    fun `check that omitted optional fields fall back to their defaults`() {
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
