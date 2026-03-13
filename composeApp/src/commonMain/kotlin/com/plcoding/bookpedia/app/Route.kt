package com.plcoding.bookpedia.book.presentation.book_detail

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object BookGraph : Route

    @Serializable
    data object BookList : Route

    @Serializable
    data class BookDetail(val id: String) : Route
}
