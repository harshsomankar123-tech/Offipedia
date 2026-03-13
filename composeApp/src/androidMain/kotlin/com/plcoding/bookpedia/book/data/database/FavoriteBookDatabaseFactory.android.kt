package com.plcoding.bookpedia.book.data.database

import android.content.Context
import androidx.room.*

actual class FavoriteBookDatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<FavoriteBookDatabase> {
        val dbFile = context.getDatabasePath(FavoriteBookDatabase.DB_NAME)
        return Room.databaseBuilder<FavoriteBookDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
    }
}
