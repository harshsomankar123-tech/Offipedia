package com.plcoding.bookpedia.book.data.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SearchResponseDto(
    val docs: List<SearchedBookDto>
)

@Serializable
data class SearchedBookDto(
    @SerialName("key") val key: String,
    @SerialName("title") val title: String,
    @SerialName("language") val language: List<String>? = null,
    @SerialName("author_name") val author_name: List<String>? = null,
    @SerialName("author_key") val author_key: List<String>? = null,
    @SerialName("cover_i") val cover_i: Int? = null,
    @SerialName("cover_edition_key") val cover_edition_key: String? = null,
    @SerialName("first_publish_year") val first_publish_year: Int? = null,
    @SerialName("ratings_average") val ratings_average: Double? = null,
    @SerialName("ratings_count") val ratings_count: Int? = null,
    @SerialName("number_of_pages_median") val number_of_pages_median: Int? = null,
    @SerialName("edition_count") val edition_count: Int? = null
)

@Serializable
data class BookWorkDto(
    @Serializable(with = BookDescriptionSerializer::class)
    val description: String? = null
)
