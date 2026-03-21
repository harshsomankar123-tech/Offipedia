package com.plcoding.bookpedia.book.domain

import com.plcoding.bookpedia.book.data.network.GeminiClient
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.map

interface BookTutorRepository {
    suspend fun getBookSummary(book: Book): Result<String, DataError.Remote>
    suspend fun getRecommendations(book: Book): Result<List<String>, DataError.Remote>
}

class BookTutorRepositoryImpl(
    private val geminiClient: GeminiClient
) : BookTutorRepository {

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
