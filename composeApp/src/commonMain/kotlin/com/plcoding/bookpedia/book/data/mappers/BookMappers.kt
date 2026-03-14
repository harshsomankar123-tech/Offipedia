package com.plcoding.bookpedia.book.data.mappers

import com.plcoding.bookpedia.book.data.database.BookEntity
import com.plcoding.bookpedia.book.data.network.SearchedBookDto
import com.plcoding.bookpedia.book.domain.Book

fun SearchedBookDto.toBook(): Book {
    return Book(
        id = key.substringAfterLast("/"),
        title = title,
        imageUrl = when {
            cover_edition_key != null -> "https://covers.openlibrary.org/b/olid/${cover_edition_key}-L.jpg"
            cover_i != null -> "https://covers.openlibrary.org/b/id/${cover_i}-L.jpg"
            else -> null
        },
        authors = author_name ?: emptyList(),
        description = null,
        languages = language ?: emptyList(),
        firstPublishYear = first_publish_year?.toString(),
        averageRating = ratings_average,
        ratingCount = ratings_count,
        numPages = number_of_pages_median,
        numEditions = edition_count ?: 0,
        coverEditionKey = cover_edition_key
    )
}

fun Book.toBookEntity(): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        languages = languages,
        authors = authors,
        firstPublishYear = firstPublishYear,
        ratingsAverage = averageRating,
        ratingsCount = ratingCount,
        numPages = numPages,
        numEditions = numEditions,
        coverEditionKey = coverEditionKey
    )
}

fun BookEntity.toBook(): Book {
    return Book(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        languages = languages,
        authors = authors,
        firstPublishYear = firstPublishYear,
        averageRating = ratingsAverage,
        ratingCount = ratingsCount,
        numPages = numPages,
        numEditions = numEditions,
        coverEditionKey = coverEditionKey
    )
}
