package com.plcoding.bookpedia

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class SampleRobolectricTest {
    @Test
    fun testRobolectric() {
        assertEquals(4, 2 + 2)
    }
}
