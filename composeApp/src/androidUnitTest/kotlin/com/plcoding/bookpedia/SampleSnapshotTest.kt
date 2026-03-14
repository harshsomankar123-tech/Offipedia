package com.plcoding.bookpedia

import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import androidx.compose.material3.Text

class SampleSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi()

    @Test
    fun testSnapshot() {
        paparazzi.snapshot {
            Text("Hello Paparazzi!")
        }
    }
}
