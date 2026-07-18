package com.nyvoratech.composebase.data.remote.dto

import kotlinx.serialization.Serializable

/** Wire format returned by the REST API. Field names mirror the JSON payload. */
@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String? = null
)
