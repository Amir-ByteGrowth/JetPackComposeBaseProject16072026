package com.nyvoratech.composebase.core.network


sealed interface Resource<out T> {

    data class Success<T>(
        val data: T
    ) : Resource<T>

    data class Error(
        val error: AppError
    ) : Resource<Nothing>
}

fun AppError.toUserMessage(): String {
    return when (this) {

        AppError.NoInternet ->
            "Please check your internet connection."

        AppError.NetworkRequestFailed ->
            "Unable to connect to the server. Please try again."

        AppError.Unauthorized ->
            "Your session has expired."

        AppError.Forbidden ->
            "You don't have permission to perform this action."

        AppError.NotFound ->
            "The requested resource was not found."

        AppError.Server ->
            "Something went wrong on the server."

        AppError.Unknown ->
            "Something went wrong. Please try again."

        is AppError.Api ->
            message ?: "Something went wrong. Please try again."

        is AppError.Firebase ->
            firebaseErrorMessage(this.code, this.message)
    }
}

private fun firebaseErrorMessage(
    code: String?, message: String?
): String {

    return when (code) {

        "ERROR_INVALID_EMAIL" ->
            "Please enter a valid email address."
        "ERROR_INVALID_CREDENTIAL" ->
            "Invalid credentials"

        "ERROR_USER_NOT_FOUND" ->
            "No account exists with this email."

        "ERROR_WRONG_PASSWORD" ->
            "Incorrect email or password."

        "ERROR_EMAIL_ALREADY_IN_USE" ->
            "An account already exists with this email."

        "ERROR_WEAK_PASSWORD" ->
            "Please choose a stronger password."

        "ERROR_NETWORK_REQUEST_FAILED" ->
            "Please check your internet connection."

        else ->
            message ?: "Authentication failed. Please try again."
    }
}