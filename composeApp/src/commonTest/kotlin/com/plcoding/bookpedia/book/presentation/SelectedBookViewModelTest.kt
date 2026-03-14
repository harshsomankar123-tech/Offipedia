package com.plcoding.bookpedia.book.presentation

import com.plcoding.bookpedia.book.domain.Book
import kotlin.test.*

class SelectedBookViewModelTest {

    private lateinit var viewModel: SelectedBookViewModel

    @BeforeTest
    fun setUp() {
        viewModel = SelectedBookViewModel()
    }

    @Test
    fun testInitialState() {
        assertNull(viewModel.selectedBook.value)
    }

    @Test
    fun testOnSelectBook() {
        val book = Book(
            id = "1",
            title = "Kotlin",
            description = null,
            imageUrl = "",
            authors = emptyList(),
            languages = emptyList(),
            firstPublishYear = null,
            averageRating = null,
            ratingCount = null,
            numPages = null,
            numEditions = 0,
            coverEditionKey = null
        )
        viewModel.onSelectBook(book)
        assertEquals(book, viewModel.selectedBook.value)

        viewModel.onSelectBook(null)
        assertNull(viewModel.selectedBook.value)
    }
}
