package com.plcoding.bookpedia.book.presentation

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.presentation.UiText
import offipedia.composeapp.generated.resources.Res
import offipedia.composeapp.generated.resources.error_disk_full
import offipedia.composeapp.generated.resources.error_no_internet
import offipedia.composeapp.generated.resources.error_serialization
import offipedia.composeapp.generated.resources.error_unknown

fun DataError.toUiText(): UiText {
    val res = when(this) {
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.SERVICE_UNAVAILABLE -> Res.string.error_no_internet
        DataError.Remote.CLIENT_ERROR -> Res.string.error_unknown
        DataError.Remote.SERVER_ERROR -> Res.string.error_unknown
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization
        DataError.Remote.UNKNOWN -> Res.string.error_unknown
    }

    return UiText.StringResourceId(res)
}
