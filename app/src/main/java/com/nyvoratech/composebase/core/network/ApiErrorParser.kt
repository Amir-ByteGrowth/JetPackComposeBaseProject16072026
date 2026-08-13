package com.nyvoratech.composebase.core.network

import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ApiErrorParser @Inject constructor(
    private val json: Json
) {

    fun parse(
        errorBody: String?
    ): NetworkResponseError? {

        if (errorBody.isNullOrBlank()) {
            return null
        }

        return runCatching {
            json.decodeFromString<NetworkResponseError>(
                errorBody
            )
        }.getOrNull()
    }
}