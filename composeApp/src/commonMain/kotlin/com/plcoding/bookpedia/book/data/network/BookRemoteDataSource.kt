package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface BookRemoteDataSource {
    suspend fun searchBooks(
        query: String,
        resultLimit: Int? = null
    ): Result<SearchResponseDto, DataError.Remote>

    suspend fun getBookDetails(bookWorkId: String): Result<BookWorkDto, DataError.Remote>
}
