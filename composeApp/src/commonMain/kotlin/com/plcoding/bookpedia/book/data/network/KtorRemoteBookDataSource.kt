package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException

private const val BASE_URL = "https://openlibrary.org"

class KtorRemoteBookDataSource(
    private val httpClient: HttpClient
) {
    suspend fun searchBooks(
        query: String,
        resultLimit: Int? = null
    ): Result<SearchResponseDto, DataError.Remote> {
        return try {
            val response = httpClient.get(
                urlString = "$BASE_URL/search.json"
            ) {
                parameter("q", query)
                parameter("limit", resultLimit)
                parameter("language", "eng")
                parameter("fields", "key,title,author_name,author_key,first_publish_year,ratings_average,ratings_count,number_of_pages_median,edition_count,language,cover_i,cover_edition_key")
            }
            Result.Success(response.body())
        } catch (e: UnresolvedAddressException) {
            Result.Error(DataError.Remote.SERVICE_UNAVAILABLE)
        } catch (e: SerializationException) {
            Result.Error(DataError.Remote.SERIALIZATION)
        } catch (e: ClientRequestException) {
            Result.Error(DataError.Remote.CLIENT_ERROR)
        } catch (e: ServerResponseException) {
            Result.Error(DataError.Remote.SERVER_ERROR)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getBookDetails(bookWorkId: String): Result<BookWorkDto, DataError.Remote> {
        return try {
            val response = httpClient.get(
                urlString = "$BASE_URL/works/$bookWorkId.json"
            )
            Result.Success(response.body())
        } catch (e: UnresolvedAddressException) {
            Result.Error(DataError.Remote.SERVICE_UNAVAILABLE)
        } catch (e: SerializationException) {
            Result.Error(DataError.Remote.SERIALIZATION)
        } catch (e: ClientRequestException) {
            Result.Error(DataError.Remote.CLIENT_ERROR)
        } catch (e: ServerResponseException) {
            Result.Error(DataError.Remote.SERVER_ERROR)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}
