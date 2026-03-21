package com.plcoding.bookpedia.book.domain

import com.plcoding.bookpedia.book.data.network.GeminiClient
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.map

interface BookTutorRepository {
    val lastError: String?
    suspend fun getBookSummary(book: Book): Result<String, DataError.Remote>
    suspend fun getRecommendations(book: Book): Result<List<String>, DataError.Remote>
    suspend fun getSearchSummary(query: String, books: List<Book>): Result<String, DataError.Remote>
    suspend fun getCustomResponse(prompt: String): Result<String, DataError.Remote>
}

class BookTutorRepositoryImpl(
    private val geminiClient: GeminiClient
) : BookTutorRepository {
    override val lastError: String?
        get() = geminiClient.lastError

    override suspend fun getCustomResponse(prompt: String): Result<String, DataError.Remote> {
        return geminiClient.generateContent(prompt)
    }

    override suspend fun getSearchSummary(query: String, books: List<Book>): Result<String, DataError.Remote> {
        val titles = books.take(5).joinToString(", ") { it.title }
        val prompt = """
            The user searched for "$query" and found these books: $titles. 
            Provide a very brief (1-2 sentences) summary of what these books are generally about and what the user might find interesting.
        """.trimIndent()
        return geminiClient.generateContent(prompt)
    }

    override suspend fun getBookSummary(book: Book): Result<String, DataError.Remote> {
        val prompt = """
            Provide a concise and engaging summary for the book "${book.title}" by ${book.authors.joinToString()}. 
            Focus on the main themes and why it's worth reading.
        """.trimIndent()
        return geminiClient.generateContent(prompt)
    }

    override suspend fun getRecommendations(book: Book): Result<List<String>, DataError.Remote> {
        val prompt = """
            Based on the book "${book.title}" by ${book.authors.joinToString()}, recommend 3 similar books. 
            Provide only the titles and authors of the recommended books, one per line.
        """.trimIndent()
        
        return geminiClient.generateContent(prompt).map { text ->
            text.lines()
                .filter { it.isNotBlank() }
                .map { it.trim().removePrefix("- ").removePrefix("1. ").removePrefix("2. ").removePrefix("3. ") }
                .take(3)
        }
    }
}
