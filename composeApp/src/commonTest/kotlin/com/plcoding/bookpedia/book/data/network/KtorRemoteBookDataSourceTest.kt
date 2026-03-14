package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.*

class KtorRemoteBookDataSourceTest {

    private fun createMockHttpClient(
        content: String,
        status: HttpStatusCode = HttpStatusCode.OK
    ): HttpClient {
        return HttpClient(MockEngine { request ->
            respond(
                content = content,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    @Test
    fun testSearchBooksSuccess() = runTest {
        val jsonResponse = """
            {
                "docs": [
                    {
                        "key": "/works/OL12345W",
                        "title": "Kotlin Programming",
                        "author_name": ["John Doe"],
                        "author_key": ["OL12345A"],
                        "first_publish_year": 2021,
                        "ratings_average": 4.5,
                        "ratings_count": 100,
                        "number_of_pages_median": 300,
                        "edition_count": 5,
                        "language": ["eng"],
                        "cover_i": 12345,
                        "cover_edition_key": "OL12345M"
                    }
                ]
            }
        """.trimIndent()

        val dataSource = KtorRemoteBookDataSource(createMockHttpClient(jsonResponse))
        val result = dataSource.searchBooks("kotlin")

        assertTrue(result is Result.Success)
        val books = (result as Result.Success).data.docs
        assertEquals(1, books.size)
        assertEquals("Kotlin Programming", books[0].title)
    }

    @Test
    fun testSearchBooksGenericError() = runTest {
        val dataSource = KtorRemoteBookDataSource(createMockHttpClient("", HttpStatusCode.InternalServerError))
        val result = dataSource.searchBooks("kotlin")

        assertTrue(result is Result.Error)
        assertEquals(DataError.Remote.SERVER_ERROR, (result as Result.Error).error)
    }

    @Test
    fun testGetBookDetailsSuccess() = runTest {
        val jsonResponse = """
            {
                "description": "A book about Kotlin"
            }
        """.trimIndent()

        val dataSource = KtorRemoteBookDataSource(createMockHttpClient(jsonResponse))
        val result = dataSource.getBookDetails("OL12345W")

        assertTrue(result is Result.Success)
        assertEquals("A book about Kotlin", (result as Result.Success).data.description)
    }
}
