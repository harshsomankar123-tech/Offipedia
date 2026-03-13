package com.plcoding.bookpedia

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.plcoding.bookpedia.app.App
import com.plcoding.bookpedia.di.platformModule
import com.plcoding.bookpedia.di.sharedModule
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(platformModule, sharedModule)
    }
    
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Offipedia",
        ) {
            App()
        }
    }
}
