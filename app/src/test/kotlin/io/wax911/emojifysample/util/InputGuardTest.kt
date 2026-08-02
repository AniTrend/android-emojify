package io.wax911.emojifysample.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputGuardTest {

    @Test
    fun isValidInput_returnsTrueForNonBlankText() {
        assertTrue("hello :smile:".isValidInput())
    }

    @Test
    fun isValidInput_returnsFalseForNull() {
        val input: String? = null
        assertFalse(input.isValidInput())
    }

    @Test
    fun isValidInput_returnsFalseForEmptyString() {
        assertFalse("".isValidInput())
    }

    @Test
    fun isValidInput_returnsFalseForWhitespaceOnly() {
        assertFalse("   \t\n".isValidInput())
    }
}
