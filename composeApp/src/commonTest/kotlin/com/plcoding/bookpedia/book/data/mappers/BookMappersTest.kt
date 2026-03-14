package com.plcoding.bookpedia.book.data.mappers

import com.plcoding.bookpedia.book.data.database.BookEntity
import com.plcoding.bookpedia.book.data.network.SearchedBookDto
import com.plcoding.bookpedia.book.domain.Book
import kotlin.test.*

class BookMappersTest {

    @Test
    fun testSearchedBookDtoToBookWithCoverEditionKey() {
        val dto = SearchedBookDto(
            key = "/works/OL123W",
            title = "Kotlin",
            author_name = listOf("Author"),
            author_key = listOf("A1"),
            first_publish_year = 2021,
            ratings_average = 4.0,
            ratings_count = 10,
            number_of_pages_median = 100,
            edition_count = 1,
            language = listOf("en"),
            cover_i = 1,
            cover_edition_key = "COVER1"
        )

        val book = dto.toBook()

        assertEquals("OL123W", book.id)
        assertEquals("Kotlin", book.title)
        assertEquals("https://covers.openlibrary.org/b/olid/COVER1-L.jpg", book.imageUrl)
        assertEquals("COVER1", book.coverEditionKey)
    }

    @Test
    fun testSearchedBookDtoToBookWithCoverI() {
        val dto = SearchedBookDto(
            key = "/works/OL123W",
            title = "Kotlin",
            author_name = listOf("Author"),
            author_key = listOf("A1"),
            first_publish_year = 2021,
            ratings_average = 4.0,
            ratings_count = 10,
            number_of_pages_median = 100,
            edition_count = 1,
            language = listOf("en"),
            cover_i = 123,
            cover_edition_key = null
        )

        val book = dto.toBook()

        assertEquals("https://covers.openlibrary.org/b/id/123-L.jpg", book.imageUrl)
        assertNull(book.coverEditionKey)
    }

    @Test
    fun testBookToBookEntity() {
        val book = Book(
            id = "1",
            title = "Title",
            description = "Desc",
            imageUrl = "url",
            authors = listOf("A"),
            languages = listOf("L"),
            firstPublishYear = "2021",
            averageRating = 4.5,
            ratingCount = 100,
            numPages = 200,
            numEditions = 5,
            coverEditionKey = "key"
        )

        val entity = book.toBookEntity()

        assertEquals(book.id, entity.id)
        assertEquals(book.title, entity.title)
        assertEquals(book.description, entity.description)
        assertEquals(book.imageUrl, entity.imageUrl)
        assertEquals(book.authors, entity.authors)
        assertEquals(book.languages, entity.languages)
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
            id = "1",
            title = "Title",
            description = "Desc",
            imageUrl = "url",
            authors = listOf("A"),
            languages = listOf("L"),
            firstPublishYear = "2021",
            ratingsAverage = 4.5,
            ratingsCount = 100,
            numPages = 200,
            numEditions = 5,
            coverEditionKey = "key"
        )

        val book = entity.toBook()

        assertEquals(entity.id, book.id)
        assertEquals(entity.title, book.title)
        assertEquals(entity.description, book.description)
        assertEquals(entity.imageUrl, book.imageUrl)
        assertEquals(entity.authors, book.authors)
        assertEquals(entity.languages, book.languages)
        assertEquals(entity.firstPublishYear, book.firstPublishYear)
        assertEquals(entity.ratingsAverage, book.averageRating)
        assertEquals(entity.ratingsCount, book.ratingCount)
        assertEquals(entity.numPages, book.numPages)
        assertEquals(entity.numEditions, book.numEditions)
        assertEquals(entity.coverEditionKey, book.coverEditionKey)
    }
}
