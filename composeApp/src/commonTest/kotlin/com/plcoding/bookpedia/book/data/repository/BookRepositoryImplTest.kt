package com.plcoding.bookpedia.book.data.repository

import com.plcoding.bookpedia.book.data.database.BookDao
import com.plcoding.bookpedia.book.data.database.BookEntity
import com.plcoding.bookpedia.book.data.network.BookWorkDto
import com.plcoding.bookpedia.book.data.network.KtorRemoteBookDataSource
import com.plcoding.bookpedia.book.data.network.SearchResponseDto
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.HttpHeaders
import kotlin.test.*

class FakeBookDao : BookDao {
    private val books = MutableStateFlow<List<BookEntity>>(emptyList())
    
    override suspend fun upsert(book: BookEntity) {
        val current = books.value.toMutableList()
        current.removeAll { it.id == book.id }
        current.add(book)
        books.value = current
    }

    override fun getFavoriteBooks(): Flow<List<BookEntity>> = books

    override suspend fun getFavoriteBook(id: String): BookEntity? {
        return books.value.find { it.id == id }
    }

    override suspend fun deleteFavoriteBook(id: String) {
        val current = books.value.toMutableList()
        current.removeAll { it.id == id }
        books.value = current
    }
}

class FakeRemoteBookDataSource : KtorRemoteBookDataSource(mockkHttpClient()) {
    var searchResult: Result<SearchResponseDto, DataError.Remote> = Result.Error(DataError.Remote.UNKNOWN)
    var detailsResult: Result<BookWorkDto, DataError.Remote> = Result.Error(DataError.Remote.UNKNOWN)

    override suspend fun searchBooks(query: String, resultLimit: Int?): Result<SearchResponseDto, DataError.Remote> {
        return searchResult
    }

    override suspend fun getBookDetails(bookWorkId: String): Result<BookWorkDto, DataError.Remote> {
        return detailsResult
    }
}

// Helper to satisfy constructor
private fun mockkHttpClient() = io.ktor.client.HttpClient(io.ktor.client.engine.mock.MockEngine { 
    respond("") 
})

class BookRepositoryImplTest {

    private lateinit var remoteBookDataSource: FakeRemoteBookDataSource
    private lateinit var favoriteBookDao: FakeBookDao
    private lateinit var repository: BookRepositoryImpl

    @BeforeTest
    fun setUp() {
        remoteBookDataSource = FakeRemoteBookDataSource()
        favoriteBookDao = FakeBookDao()
        repository = BookRepositoryImpl(remoteBookDataSource, favoriteBookDao)
    }

    @Test
    fun testGetBookDescriptionFromLocalSuccess() = runTest {
        val bookId = "1"
        val entity = BookEntity(id = bookId, title = "Title", description = "Local Desc", imageUrl = "", languages = emptyList(), authors = emptyList(), firstPublishYear = null, ratingsAverage = null, ratingsCount = null, numPages = null, numEditions = 0, coverEditionKey = null)
        
        favoriteBookDao.upsert(entity)

        val result = repository.getBookDescription(bookId)

        assertTrue(result is Result.Success)
        assertEquals("Local Desc", (result as Result.Success).data)
    }

    @Test
    fun testGetBookDescriptionFromRemoteSuccess() = runTest {
        val bookId = "1"
        val dto = BookWorkDto(description = "Remote Desc")
        
        remoteBookDataSource.detailsResult = Result.Success(dto)

        val result = repository.getBookDescription(bookId)

        assertTrue(result is Result.Success)
        assertEquals("Remote Desc", (result as Result.Success).data)
    }

    @Test
    fun testMarkAsFavoriteSuccess() = runTest {
        val book = Book(id = "1", title = "Title", description = null, imageUrl = "", authors = emptyList(), languages = emptyList(), firstPublishYear = null, averageRating = null, ratingCount = null, numPages = null, numEditions = 0, coverEditionKey = null)
        
        val result = repository.markAsFavorite(book)

        assertTrue(result is Result.Success)
        val saved = favoriteBookDao.getFavoriteBook("1")
        assertNotNull(saved)
        assertEquals("1", saved.id)
    }

    @Test
    fun testIsBookFavorite() = runTest {
        val bookId = "1"
        val entity = BookEntity(id = bookId, title = "Title", description = null, imageUrl = "", languages = emptyList(), authors = emptyList(), firstPublishYear = null, ratingsAverage = null, ratingsCount = null, numPages = null, numEditions = 0, coverEditionKey = null)
        
        favoriteBookDao.upsert(entity)

        val isFavorite = repository.isBookFavorite(bookId).first()

        assertTrue(isFavorite)
    }
}
