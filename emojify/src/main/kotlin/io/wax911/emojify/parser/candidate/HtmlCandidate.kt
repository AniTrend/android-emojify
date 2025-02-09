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
