package com.nyvoratech.composebase.core.common

interface CrashReporter {

    fun recordException(
        throwable: Throwable,
        message: String? = null
    )

    fun setCustomKey(
        key: String,
        value: String
    )
}