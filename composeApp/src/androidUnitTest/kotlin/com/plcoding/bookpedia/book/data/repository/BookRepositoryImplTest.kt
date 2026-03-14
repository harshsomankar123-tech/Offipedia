package com.plcoding.bookpedia.book.data.repository

import com.plcoding.bookpedia.book.data.database.BookDao
import com.plcoding.bookpedia.book.data.database.BookEntity
import com.plcoding.bookpedia.book.data.network.KtorRemoteBookDataSource
import com.plcoding.bookpedia.book.data.network.SearchResponseDto
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.core.domain.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookRepositoryImplTest {

    private lateinit var repository: BookRepositoryImpl
    private val remoteDataSource = mockk<KtorRemoteBookDataSource>()
    private val bookDao = mockk<BookDao>()

    @Before
    fun setUp() {
        repository = BookRepositoryImpl(remoteDataSource, bookDao)
    }

    @Test
    fun testSearchBooksSuccess() = runBlocking {
        val dtoResponse = SearchResponseDto(docs = emptyList())
        coEvery { remoteDataSource.searchBooks(any()) } returns Result.Success(dtoResponse)

        val result = repository.searchBooks("kotlin")

        assertTrue(result is Result.Success)
        coVerify { remoteDataSource.searchBooks("kotlin") }
    }

    @Test
    fun testGetFavoriteBooks() = runBlocking {
        val entities = listOf(
            BookEntity(id = "1", title = "Book 1", description = null, imageUrl = "", authors = emptyList(), languages = emptyList(), firstPublishYear = null, ratingsAverage = null, ratingsCount = null, numPages = null, numEditions = 0, coverEditionKey = null)
        )
        coEvery { bookDao.getFavoriteBooks() } returns flowOf(entities)

        val favoriteBooks = repository.getFavoriteBooks().first()

        assertEquals(1, favoriteBooks.size)
        assertEquals("1", favoriteBooks[0].id)
    }

    @Test
    fun testIsBookFavorite() = runBlocking {
        val entities = listOf(
            BookEntity(id = "1", title = "Book 1", description = null, imageUrl = "", authors = emptyList(), languages = emptyList(), firstPublishYear = null, ratingsAverage = null, ratingsCount = null, numPages = null, numEditions = 0, coverEditionKey = null)
        )
        coEvery { bookDao.getFavoriteBooks() } returns flowOf(entities)

        val isFavorite = repository.isBookFavorite("1").first()

        assertTrue(isFavorite)
    }

    @Test
    fun testMarkAsFavorite() = runBlocking {
        val book = Book(id = "1", title = "Book 1", description = null, imageUrl = "", authors = emptyList(), languages = emptyList(), firstPublishYear = null, averageRating = null, ratingCount = null, numPages = null, numEditions = 0, coverEditionKey = null)
        coEvery { bookDao.upsert(any()) } returns Unit

        val result = repository.markAsFavorite(book)

        assertTrue(result is Result.Success)
        coVerify { bookDao.upsert(any()) }
    }
}
