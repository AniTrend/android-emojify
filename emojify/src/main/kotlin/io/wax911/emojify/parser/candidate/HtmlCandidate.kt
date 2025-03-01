/*
 * Copyright 2025 AniTrend
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

package io.wax911.emojify.parser.candidate

import io.wax911.emojify.contract.model.IEmoji
import io.wax911.emojify.parser.candidate.contract.ICandidate
import io.wax911.emojify.util.Fitzpatrick

internal data class HtmlCandidate(
    override val emoji: IEmoji,
    override val fitzpatrick: Fitzpatrick?,
    val startIndex: Int,
    val endIndex: Int,
) : ICandidate
