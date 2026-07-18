package com.nyvoratech.composebase.core.common

/**
 * Generic wrapper for any operation that can be loading, succeed with data,
 * or fail with an error. Used by repositories to communicate state up to
 * the domain/UI layers without leaking Retrofit/Room exception types.
 */
sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
}

/**
 * Runs [block] and maps common exceptions into a [Resource.Error], so callers
 * (repositories) don't need repetitive try/catch blocks around every call.
 */
suspend fun <T> safeCall(block: suspend () -> T): Resource<T> = try {
    Resource.Success(block())
} catch (e: java.io.IOException) {
    Resource.Error(message = "Network error. Please check your connection.", throwable = e)
} catch (e: retrofit2.HttpException) {
    Resource.Error(message = "Server error (${e.code()}).", throwable = e)
} catch (e: Exception) {
    Resource.Error(message = e.message ?: "Unknown error", throwable = e)
}
