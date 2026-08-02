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

package io.wax911.emojifysample

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.parser.parseToHtmlDecimal
import io.wax911.emojify.parser.parseToHtmlHexadecimal
import io.wax911.emojify.parser.parseToUnicode
import io.wax911.emojify.serializer.kotlinx.KotlinxDeserializer
import io.wax911.emojifysample.ui.theme.EmojifyTheme
import io.wax911.emojifysample.util.isValidInput

/**
 * The three conversions exposed by the sample screen, mapped 1:1 to the
 * [EmojiManager] parser extensions.
 */
internal enum class ConversionAction {
    EMOJI,
    HTML,
    HEX,
}

/**
 * Tier 1 single-column screen: a top app bar, a multiline input field, and a fixed row of
 * three conversion buttons. Conversions replace the input text in place, synchronously.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(emojiManager: EmojiManager, modifier: Modifier = Modifier) {
    var input by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    fun convert(action: ConversionAction) {
        val textToConvert = input
        if (!textToConvert.isValidInput()) {
            Toast.makeText(
                context,
                context.getString(R.string.empty_input_feedback),
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        input =
            when (action) {
                ConversionAction.EMOJI -> emojiManager.parseToUnicode(textToConvert)
                ConversionAction.HTML -> emojiManager.parseToHtmlDecimal(textToConvert)
                ConversionAction.HEX -> emojiManager.parseToHtmlHexadecimal(textToConvert)
            }
    }

    Scaffold(
        topBar = {
            // material3 1.4.0: the single-row small app bar is the base TopAppBar variant.
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(16.dp),
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.input_label)) },
                    placeholder = { Text(stringResource(R.string.input_hint)) },
                    minLines = 5,
                )
            }
            ConversionActions(
                onEmojiClick = { convert(ConversionAction.EMOJI) },
                onHtmlClick = { convert(ConversionAction.HTML) },
                onHexClick = { convert(ConversionAction.HEX) },
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}

/**
 * Fixed row of the three conversion buttons, kept outside the scrollable input area so
 * repeated taps do not chase a moving target.
 */
@Composable
private fun ConversionActions(onEmojiClick: () -> Unit, onHtmlClick: () -> Unit, onHexClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ConversionButton(
            label = stringResource(R.string.convert_to_emoji),
            description = stringResource(R.string.convert_to_emoji_description),
            onClick = onEmojiClick,
            modifier = Modifier.weight(1f),
        )
        ConversionButton(
            label = stringResource(R.string.convert_to_html),
            description = stringResource(R.string.convert_to_html_description),
            onClick = onHtmlClick,
            modifier = Modifier.weight(1f),
        )
        ConversionButton(
            label = stringResource(R.string.convert_to_hex),
            description = stringResource(R.string.convert_to_hex_description),
            onClick = onHexClick,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * Single conversion button with a minimum 48dp touch target and a TalkBack description.
 */
@Composable
private fun ConversionButton(label: String, description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier =
        modifier
            .height(48.dp)
            .semantics { contentDescription = description },
    ) {
        Text(text = label)
    }
}

@Preview(showBackground = true, name = "Main screen")
@Composable
private fun MainScreenPreview() {
    val context = LocalContext.current
    val emojiManager = remember { EmojiManager.create(context, KotlinxDeserializer()) }
    EmojifyTheme {
        MainScreen(emojiManager = emojiManager)
    }
}
