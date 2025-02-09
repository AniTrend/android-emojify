package io.wax911.emojify.parser.candidate.contract

import io.wax911.emojify.contract.model.IEmoji
import io.wax911.emojify.util.Fitzpatrick

interface ICandidate {
    val emoji: IEmoji?
    val fitzpatrick: Fitzpatrick?
}
