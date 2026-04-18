package com.plcoding.bookpedia

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationUtilsTest {

    @Test
    fun testValidationLogic() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"))
        assertTrue(ValidationUtils.isValidPassword("password123"))
        assertTrue(ValidationUtils.isAlpha("Hello"))
        assertTrue(ValidationUtils.isNumeric("12345"))
        assertTrue(ValidationUtils.isNotEmpty("Not empty"))
        assertEquals("HELLO", ValidationUtils.toUpper("hello"))
        assertEquals("hello", ValidationUtils.toLower("HELLO"))
        assertEquals("trimmed", ValidationUtils.trimText("  trimmed  "))
        assertEquals("detset", ValidationUtils.reverseText("tested"))
        
        // intentionally NOT calling ValidationUtils.uncoveredFunction()
    }
}
