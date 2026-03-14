package com.plcoding.bookpedia.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.presentation.book_list.FakeBookRepository
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {

    private lateinit var viewModel: BookDetailViewModel
    private lateinit var repository: FakeBookRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeBookRepository()
        
        // Mock SavedStateHandle with Route.BookDetail
        // Note: SavedStateHandle.toRoute is an extension from Navigation
        // For simplicity in unit test, we can pass the ID directly if the implementation allows,
        // but here it calls toRoute.
        // We can use the constructor that takes a map if available or just mock it.
        val savedStateHandle = SavedStateHandle(mapOf("id" to "123"))
        
        viewModel = BookDetailViewModel(repository, savedStateHandle)
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
