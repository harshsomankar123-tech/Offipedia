package com.plcoding.bookpedia.book.data.database

import androidx.room.*
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

actual class FavoriteBookDatabaseFactory {
    actual fun create(): RoomDatabase.Builder<FavoriteBookDatabase> {
        val dbFile = NSHomeDirectory() + "/" + FavoriteBookDatabase.DB_NAME
        return Room.databaseBuilder<FavoriteBookDatabase>(
            name = dbFile,
            factory = { FavoriteBookDatabase::class.instantiateImpl() }
        ).setDriver(BundledSQLiteDriver())
    }
}
