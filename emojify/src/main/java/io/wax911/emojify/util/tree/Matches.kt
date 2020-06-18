package io.wax911.emojify.util.tree

/**
 * Emoji matching state representation
 */
sealed class Matches {
    object EXACTLY : Matches()
    object POSSIBLY : Matches()
    object IMPOSSIBLE : Matches()

    fun exactMatch() = this is EXACTLY
    fun impossibleMatch() = this is IMPOSSIBLE
}