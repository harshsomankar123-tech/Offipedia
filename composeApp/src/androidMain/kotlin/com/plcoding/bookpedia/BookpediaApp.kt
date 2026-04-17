package com.plcoding.bookpedia

import android.app.Application
import com.plcoding.bookpedia.di.platformModule
import com.plcoding.bookpedia.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BookpediaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@BookpediaApp)
            modules(platformModule, sharedModule)
        }
    }
}
