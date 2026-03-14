package com.plcoding.bookpedia.book.data.mappers

import com.plcoding.bookpedia.book.data.database.BookEntity
import com.plcoding.bookpedia.book.data.network.SearchedBookDto
import com.plcoding.bookpedia.book.domain.Book
import kotlin.test.Test
import kotlin.test.assertEquals

class BookMappersTest {

    @Test
    fun testSearchedBookDtoToBook() {
        val dto = SearchedBookDto(
            key = "/works/OL12345W",
            title = "Kotlin Multiplatform",
            language = listOf("en"),
            author_name = listOf("Philipp Lackner"),
            cover_edition_key = "OL12345M",
            first_publish_year = 2024,
            ratings_average = 4.5,
            ratings_count = 100,
            number_of_pages_median = 300,
            edition_count = 5
        )

        val book = dto.toBook()

        assertEquals("OL12345W", book.id)
        assertEquals(dto.title, book.title)
        assertEquals("https://covers.openlibrary.org/b/olid/OL12345M-L.jpg", book.imageUrl)
        assertEquals(dto.author_name, book.authors)
        assertEquals(dto.language, book.languages)
        assertEquals("2024", book.firstPublishYear)
        assertEquals(dto.ratings_average, book.averageRating)
        assertEquals(dto.ratings_count, book.ratingCount)
        assertEquals(dto.number_of_pages_median, book.numPages)
        assertEquals(dto.edition_count, book.numEditions)
    }

    @Test
    fun testBookToBookEntity() {
        val book = Book(
            id = "123",
            title = "Test Book",
            description = "Description",
            imageUrl = "url",
            languages = listOf("en"),
            authors = listOf("Author"),
            firstPublishYear = "2024",
            averageRating = 4.0,
            ratingCount = 50,
            numPages = 200,
            numEditions = 2,
            coverEditionKey = "key"
        )

        val entity = book.toBookEntity()

        assertEquals(book.id, entity.id)
        assertEquals(book.title, entity.title)
        assertEquals(book.description, entity.description)
        assertEquals(book.imageUrl, entity.imageUrl)
        assertEquals(book.languages, entity.languages)
        assertEquals(book.authors, entity.authors)
        assertEquals(book.firstPublishYear, entity.firstPublishYear)
        assertEquals(book.averageRating, entity.ratingsAverage)
        assertEquals(book.ratingCount, entity.ratingsCount)
        assertEquals(book.numPages, entity.numPages)
        assertEquals(book.numEditions, entity.numEditions)
        assertEquals(book.coverEditionKey, entity.coverEditionKey)
    }

    @Test
    fun testBookEntityToBook() {
        val entity = BookEntity(
            id = "123",
            title = "Test Book",
            description = "Description",
            imageUrl = "url",
            languages = listOf("en"),
            authors = listOf("Author"),
            firstPublishYear = "2024",
            ratingsAverage = 4.0,
            ratingsCount = 50,
            numPages = 200,
            numEditions = 2,
            coverEditionKey = "key"
        )

        val book = entity.toBook()

        assertEquals(entity.id, book.id)
        assertEquals(entity.title, book.title)
        assertEquals(entity.description, book.description)
        assertEquals(entity.imageUrl, book.imageUrl)
        assertEquals(entity.languages, book.languages)
        assertEquals(entity.authors, book.authors)
        assertEquals(entity.firstPublishYear, book.firstPublishYear)
        assertEquals(entity.ratingsAverage, book.averageRating)
        assertEquals(entity.ratingsCount, book.ratingCount)
        assertEquals(entity.numPages, book.numPages)
        assertEquals(entity.numEditions, book.numEditions)
        assertEquals(entity.coverEditionKey, book.coverEditionKey)
    }
}
