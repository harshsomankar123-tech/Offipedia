package com.plcoding.bookpedia.book.data.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.*

class BookDescriptionSerializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testDeserializePrimitive() {
        val input = "\"This is a description\""
        val result = json.decodeFromString(BookDescriptionSerializer, input)
        assertEquals("This is a description", result)
    }

    @Test
    fun testDeserializeObject() {
        val input = "{\"value\": \"This is a description in a value field\"}"
        val result = json.decodeFromString(BookDescriptionSerializer, input)
        assertEquals("This is a description in a value field", result)
    }

    @Test
    fun testDeserializeNull() {
        val input = "null"
        val result = json.decodeFromString(BookDescriptionSerializer, input)
        assertNull(result)
    }

    @Test
    fun testSerialize() {
        val input = "Description"
        val result = json.encodeToString(BookDescriptionSerializer, input)
        assertEquals("\"Description\"", result)
    }

    @Test
    fun testSerializeNull() {
        val input: String? = null
        val result = json.encodeToString(BookDescriptionSerializer, input)
        assertEquals("null", result)
    }
}
