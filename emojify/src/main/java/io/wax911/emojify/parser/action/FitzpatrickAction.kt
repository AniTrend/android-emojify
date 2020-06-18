package io.wax911.emojify.parser.action

import io.wax911.emojify.util.Fitzpatrick

/**
 * Used to indicate what should be done when a Fitzpatrick modifier is found.
 */
sealed class FitzpatrickAction {
    /** Tries to match the Fitzpatrick modifier with the previous emoji */
    object PARSE : FitzpatrickAction()
    /** Removes the Fitzpatrick modifier from the string */
    object REMOVE : FitzpatrickAction()
    /** Ignores the [Fitzpatrick] modifier (it will stay in the string) */
    object IGNORE : FitzpatrickAction()
}