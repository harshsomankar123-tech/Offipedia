package com.plcoding.bookpedia.book.data.database

import androidx.room.*

expect class FavoriteBookDatabaseFactory {
    fun create(): RoomDatabase.Builder<FavoriteBookDatabase>
}
