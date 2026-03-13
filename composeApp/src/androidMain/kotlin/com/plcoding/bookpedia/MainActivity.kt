package com.plcoding.bookpedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.plcoding.bookpedia.app.App
import com.plcoding.bookpedia.di.platformModule
import com.plcoding.bookpedia.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        startKoin {
            androidContext(applicationContext)
            modules(platformModule, sharedModule)
        }

        setContent {
            App()
        }
    }
}
