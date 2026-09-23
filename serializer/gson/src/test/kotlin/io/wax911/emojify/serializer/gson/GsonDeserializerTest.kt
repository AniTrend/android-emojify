/*
 * Copyright 2024 AniTrend
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

package io.wax911.emojify.serializer.gson

import org.junit.Assert.assertEquals
import org.junit.Test

class GsonDeserializerTest {
    @Test
    fun decodeFromStream_preservesShortCodeOrder() {
        val decoded = GsonDeserializer().decodeFromStream(MOCK_EMOJI_PAYLOAD.byteInputStream())

        assertEquals(
            listOf(
                listOf("smile", "grinning_face", "happy"),
                listOf("wave", "waving_hand"),
            ),
            decoded.map { it.shortCodes },
        )
    }

    private companion object {
        const val MOCK_EMOJI_PAYLOAD = """
            [
              {
                "description": "grinning face",
                "emoji": "😀",
                "supportsFitzpatrick": false,
                "tags": ["happy"],
                "unicode": "\\uD83D\\uDE00",
                "htmlDec": "&#128512;",
                "htmlHex": "&#x1f600;",
                "shortCodes": ["smile", "grinning_face", "happy"]
              },
              {
                "description": "waving hand",
                "emoji": "👋",
                "supportsFitzpatrick": false,
                "tags": ["hand"],
                "unicode": "\\uD83D\\uDC4B",
                "htmlDec": "&#128075;",
                "htmlHex": "&#x1f44b;",
                "shortCodes": ["wave", "waving_hand"]
              }
            ]
        """
    }
}
