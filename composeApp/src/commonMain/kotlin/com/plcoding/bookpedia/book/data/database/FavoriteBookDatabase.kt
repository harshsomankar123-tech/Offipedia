package com.plcoding.bookpedia.book.data.database

import androidx.room.*
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [BookEntity::class],
    version = 1
)
@TypeConverters(StringListTypeConverter::class)
abstract class FavoriteBookDatabase : RoomDatabase() {
    abstract val dao: BookDao

    companion object {
        const val DB_NAME = "bookpedia.db"
    }
}
