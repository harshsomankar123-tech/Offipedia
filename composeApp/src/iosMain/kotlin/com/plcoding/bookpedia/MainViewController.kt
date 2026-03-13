package com.plcoding.bookpedia

import androidx.compose.ui.window.ComposeUIViewController
import com.plcoding.bookpedia.app.App
import com.plcoding.bookpedia.di.platformModule
import com.plcoding.bookpedia.di.sharedModule
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}

fun initKoin() {
    startKoin {
        modules(platformModule, sharedModule)
    }
}
