package com.nyvoratech.composebase.core.network

sealed interface AppError {


    /**
     * Device currently has no validated internet connection.
     */
    data object NoInternet : AppError

    /**
     * Internet was available, but the API request failed
     * at the transport/network layer.
     */
    data object NetworkRequestFailed : AppError

    data object Unauthorized : AppError

    data object Forbidden : AppError

    data object NotFound : AppError

    data object Server : AppError

    data object Unknown : AppError

    data class Api(
        val code: Int?,
        val message: String?
    ) : AppError

    data class Firebase(
        val code: String?,
        val message: String?
    ) : AppError
}
