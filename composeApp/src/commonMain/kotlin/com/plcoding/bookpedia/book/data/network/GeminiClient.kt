package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.BuildConfig
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.DataError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val safetySettings: List<SafetySetting>? = null
) {
    @Serializable
    data class Content(
        val role: String = "user",
        val parts: List<Part>
    ) {
        @Serializable
        data class Part(
            val text: String
        )
    }

    @Serializable
    data class SafetySetting(
        val category: String,
        val threshold: String
    )
}

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>
) {
    @Serializable
    data class Candidate(
        val content: Content? = null,
        val finishReason: String? = null
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
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent"
    
    var lastError: String? = null
        private set

    suspend fun generateContent(prompt: String): Result<String, DataError.Remote> {
        lastError = null
        return try {
            val response = httpClient.post("$baseUrl?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(
                    contents = listOf(
                        GeminiRequest.Content(
                            parts = listOf(GeminiRequest.Content.Part(text = prompt))
                        )
                    ),
                    safetySettings = listOf(
                        GeminiRequest.SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_NONE"),
                        GeminiRequest.SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_NONE"),
                        GeminiRequest.SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_NONE"),
                        GeminiRequest.SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_NONE")
                    )
                ))
            }

            val bodyString = response.body<String>()
            println("Gemini Raw Response: $bodyString")
            
            if (response.status == HttpStatusCode.OK) {
                val geminiResponse = Json { ignoreUnknownKeys = true }.decodeFromString<GeminiResponse>(bodyString)
                val firstCandidate = geminiResponse.candidates.firstOrNull()
                val text = firstCandidate?.content?.parts?.firstOrNull()?.text
                
                if (text != null) {
                    Result.Success(text)
                } else {
                    val finishReason = firstCandidate?.finishReason
                    lastError = "API_ERROR: No text. FinishReason: $finishReason. Body: ${bodyString.take(100)}"
                    Result.Error(DataError.Remote.UNKNOWN)
                }
            } else {
                lastError = "HTTP_ERROR: ${response.status}. Body: ${bodyString.take(100)}"
                Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            lastError = "EXCEPTION: ${e.message}"
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}
