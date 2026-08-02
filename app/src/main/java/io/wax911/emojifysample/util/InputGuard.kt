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

package io.wax911.emojifysample.util

/**
 * Returns true when the input is a non-null string that contains at least one
 * non-whitespace character.
 *
 * This is the sample screen's empty input guard: null, empty, and whitespace-only input
 * shows feedback and must never reach a parser.
 */
internal fun String?.isValidInput(): Boolean = !isNullOrBlank()
