package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.BuildConfig
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.DataError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<Content>
) {
    @Serializable
    data class Content(
        val parts: List<Part>
    ) {
        @Serializable
        data class Part(
            val text: String
        )
    }
}

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>
) {
    @Serializable
    data class Candidate(
        val content: Content
    ) {
        @Serializable
        data class Content(
            val parts: List<Part>
        ) {
            @Serializable
            data class Part(
                val text: String
            )
        }
    }
}

class GeminiClient(
    private val httpClient: HttpClient
) {
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    suspend fun generateContent(prompt: String): Result<String, DataError.Remote> {
        return try {
            val response = httpClient.post("$baseUrl?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(
                    contents = listOf(
                        GeminiRequest.Content(
                            parts = listOf(GeminiRequest.Content.Part(text = prompt))
                        )
                    )
                ))
            }

            if (response.status == HttpStatusCode.OK) {
                val geminiResponse = response.body<GeminiResponse>()
                val text = geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (text != null) {
                    Result.Success(text)
                } else {
                    Result.Error(DataError.Remote.UNKNOWN)
                }
            } else {
                Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}
