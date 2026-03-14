package com.plcoding.bookpedia.book.presentation.book_list

import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

class FakeBookRepository : BookRepository {
    var searchResult: Result<List<Book>, DataError.Remote> = Result.Success(emptyList())
    var favoriteBooks = emptyList<Book>()

    override suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote> = searchResult
    override suspend fun getBookDescription(bookId: String): Result<String?, DataError> = Result.Success(null)
    override fun getFavoriteBooks(): Flow<List<Book>> = flowOf(favoriteBooks)
    override fun isBookFavorite(id: String): Flow<Boolean> = flowOf(favoriteBooks.any { it.id == id })
    override suspend fun markAsFavorite(book: Book): Result<Unit, DataError.Local> = Result.Success(Unit)
    override suspend fun deleteFromFavorites(id: String) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class BookListViewModelTest {

    private lateinit var viewModel: BookListViewModel
    private lateinit var repository: FakeBookRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeBookRepository()
        viewModel = BookListViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testOnSearchQueryChange() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }
        
        viewModel.onAction(BookListAction.OnSearchQueryChange("kotlin"))
        advanceUntilIdle()
        assertEquals("kotlin", viewModel.state.value.searchQuery)
    }

    @Test
    fun testOnTabSelected() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }
        
        viewModel.onAction(BookListAction.OnTabSelected(1))
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.selectedTabIndex)
    }

    @Test
    fun testSearchBooksSuccess() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }
        
        val books = listOf(
            Book(id = "1", title = "Kotlin", description = null, imageUrl = "", authors = emptyList(), languages = emptyList(), firstPublishYear = null, averageRating = null, ratingCount = null, numPages = null, numEditions = 0, coverEditionKey = null)
        )
        repository.searchResult = Result.Success(books)
        
        viewModel.onAction(BookListAction.OnSearchQueryChange("kotlin"))
        
        advanceUntilIdle() // Wait for debounce and searchJob
        
        assertEquals(books, viewModel.state.value.searchResults)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)
    }
}
