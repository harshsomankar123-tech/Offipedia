package com.plcoding.bookpedia.book.presentation

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.presentation.UiText
import offipedia.composeapp.generated.resources.Res
import offipedia.composeapp.generated.resources.*
import kotlin.test.*

class DataErrorToUiTextTest {

    @Test
    fun testLocalErrorToUiText() {
        assertEquals(
            Res.string.error_disk_full,
            (DataError.Local.DISK_FULL.toUiText() as UiText.StringResourceId).res
        )
        assertEquals(
            Res.string.error_unknown,
            (DataError.Local.UNKNOWN.toUiText() as UiText.StringResourceId).res
        )
    }

    @Test
    fun testRemoteErrorToUiText() {
        assertEquals(
            Res.string.error_no_internet,
            (DataError.Remote.SERVICE_UNAVAILABLE.toUiText() as UiText.StringResourceId).res
        )
        assertEquals(
            Res.string.error_serialization,
            (DataError.Remote.SERIALIZATION.toUiText() as UiText.StringResourceId).res
        )
        assertEquals(
            Res.string.error_unknown,
            (DataError.Remote.CLIENT_ERROR.toUiText() as UiText.StringResourceId).res
        )
        assertEquals(
            Res.string.error_unknown,
            (DataError.Remote.SERVER_ERROR.toUiText() as UiText.StringResourceId).res
        )
        assertEquals(
            Res.string.error_unknown,
            (DataError.Remote.UNKNOWN.toUiText() as UiText.StringResourceId).res
        )
    }
}
