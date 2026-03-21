package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeminiClientTest {

    @Test
    fun `generateContent returns success when API returns 200`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = """
                    {
                      "candidates": [{
                        "content": {
                          "parts": [{"text": "This is a test summary."}]
                        }
                      }]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val client = GeminiClient(httpClient)
        val result = client.generateContent("test prompt")

        assertTrue(result is Result.Success)
        assertEquals("This is a test summary.", (result as Result.Success).data)
    }

    @Test
    fun `generateContent returns error when API returns 500`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = "",
                status = HttpStatusCode.InternalServerError
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val client = GeminiClient(httpClient)
        val result = client.generateContent("test prompt")

        assertTrue(result is Result.Error)
    }
}
