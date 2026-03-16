package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface BookRemoteDataSource {
    /**
     * Searches remote book records that match the provided query.
     *
     * @param query The search query string to match against book records.
     * @param resultLimit Optional maximum number of results to return; `null` means no limit.
     * @return A `Result` containing a `SearchResponseDto` on success, or a `DataError.Remote` on failure.
     */
    suspend fun searchBooks(
        query: String,
        resultLimit: Int? = null
    ): Result<SearchResponseDto, DataError.Remote>

    /**
 * Fetches detailed information for a book work by its identifier.
 *
 * @param bookWorkId The identifier of the book work to retrieve.
 * @return A Result containing the `BookWorkDto` on success, or `DataError.Remote` on failure.
 */
suspend fun getBookDetails(bookWorkId: String): Result<BookWorkDto, DataError.Remote>
}
