package com.nyvoratech.composebase.core.network

import kotlinx.serialization.Serializable


@Serializable
data class NetworkResponseError(
    val code: Int? = null,
    val message: String? = null,
    val accessToken: String? = null
)