package com.plcoding.bookpedia

import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(application = BookpediaApp::class)
class BookpediaAppTest {

    @Test
    fun `onCreate should initialize Koin`() {
        // Ensure Koin is stopped before starting the test
        stopKoin()
        
        val app = ApplicationProvider.getApplicationContext<BookpediaApp>()
        app.onCreate()
        
        // We verify that Koin is started
        assertNotNull(GlobalContext.getOrNull(), "Koin should be started after Application onCreate")
    }
}
