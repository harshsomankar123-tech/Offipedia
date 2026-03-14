package com.plcoding.bookpedia.book.data.database

import kotlin.test.*

class StringListTypeConverterTest {

    @Test
    fun testFromList() {
        val list = listOf("a", "b", "c")
        val json = StringListTypeConverter.fromList(list)
        assertEquals("[\"a\",\"b\",\"c\"]", json)
    }

    @Test
    fun testFromString() {
        val json = "[\"a\",\"b\",\"c\"]"
        val list = StringListTypeConverter.fromString(json)
        assertEquals(listOf("a", "b", "c"), list)
    }

    @Test
    fun testEmptyList() {
        val list = emptyList<String>()
        val json = StringListTypeConverter.fromList(list)
        assertEquals("[]", json)
        assertEquals(list, StringListTypeConverter.fromString(json))
    }
}
