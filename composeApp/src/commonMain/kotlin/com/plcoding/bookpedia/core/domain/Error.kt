package com.plcoding.bookpedia.core.domain

sealed interface Error

sealed interface DataError: Error {
    enum class Remote: DataError {
        SERVICE_UNAVAILABLE,
        CLIENT_ERROR,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN
    }

    enum class Local: DataError {
        DISK_FULL,
        UNKNOWN
    }
}
