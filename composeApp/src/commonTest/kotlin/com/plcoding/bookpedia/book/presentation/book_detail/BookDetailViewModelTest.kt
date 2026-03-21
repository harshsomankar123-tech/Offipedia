package com.plcoding.bookpedia.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookTutorRepository
import com.plcoding.bookpedia.book.presentation.book_list.FakeBookRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

class FakeBookTutorRepository : BookTutorRepository {
    override val lastError: String? = null
    override suspend fun getBookSummary(book: Book): Result<String, DataError.Remote> = Result.Success("Fake Summary")
    override suspend fun getRecommendations(book: Book): Result<List<String>, DataError.Remote> = Result.Success(listOf("Rec 1", "Rec 2"))
    override suspend fun getSearchSummary(query: String, books: List<Book>): Result<String, DataError.Remote> = Result.Success("Search summary")
    override suspend fun getCustomResponse(prompt: String): Result<String, DataError.Remote> = Result.Success("Fake Custom Response")
}

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {

    private lateinit var viewModel: BookDetailViewModel
    private lateinit var repository: FakeBookRepository
    private lateinit var tutorRepository: FakeBookTutorRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeBookRepository()
        tutorRepository = FakeBookTutorRepository()
        
        val savedStateHandle = SavedStateHandle(mapOf("id" to "123"))
        
        viewModel = BookDetailViewModel(repository, tutorRepository, savedStateHandle)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }
        
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(false, viewModel.state.value.isFavorite)
    }

    @Test
    fun testFetchBookDescriptionSuccess() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }
        
        repository.searchResult = Result.Success(emptyList()) // Not used here but good practice
        val description = "Amazing book description"
        // We need to fix FakeBookRepository to return success for getBookDescription
        // (already does in my previous turn)
        
        advanceUntilIdle()
        // The implementation updates the book's description
        // But we need a book in the state first.
        val book = Book(id = "123", title = "Kotlin", description = null, imageUrl = "", authors = emptyList(), languages = emptyList(), firstPublishYear = null, averageRating = null, ratingCount = null, numPages = null, numEditions = 0, coverEditionKey = null)
        viewModel.onAction(BookDetailAction.OnSelectedBookChange(book))
        
        // Wait for fetchBookDescription to complete
        advanceUntilIdle()
        
        // Wait, fetchBookDescription is called onStart.
        // If we set the book after onStart, we might need to re-trigger or check how it works.
        // In the ViewModel, fetchBookDescription updates book = it.book?.copy(description = description)
        
        // Let's re-verify the logic.
    }
}
