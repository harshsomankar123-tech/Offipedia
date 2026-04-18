package com.plcoding.bookpedia

object ValidationUtils {
    fun isValidEmail(email: String): Boolean = email.contains("@")
    fun isValidPassword(password: String): Boolean = password.length >= 8
    fun isAlpha(text: String): Boolean = text.all { it.isLetter() }
    fun isNumeric(text: String): Boolean = text.all { it.isDigit() }
    fun isNotEmpty(text: String): Boolean = text.isNotEmpty()
    fun toUpper(text: String): String = text.uppercase()
    fun toLower(text: String): String = text.lowercase()
    fun trimText(text: String): String = text.trim()
    fun reverseText(text: String): String = text.reversed()
    fun uncoveredFunction(): String = "This is not tested"
}
