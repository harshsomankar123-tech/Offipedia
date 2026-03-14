package com.plcoding.bookpedia

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.material3.Text
import org.junit.Rule
import org.junit.Test

class SampleInstrumentationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHelloWorld() {
        composeTestRule.setContent {
            Text("Hello Instrumentation!")
        }

        composeTestRule.onNodeWithText("Hello Instrumentation!").assertExists()
    }
}
