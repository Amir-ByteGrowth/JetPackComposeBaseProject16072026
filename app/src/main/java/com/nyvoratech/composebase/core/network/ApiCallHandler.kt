package com.nyvoratech.composebase.core.network

import com.nyvoratech.composebase.core.common.CrashReporter
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiCallHandler @Inject constructor(
    private val apiErrorParser: ApiErrorParser,
    private val networkMonitor: NetworkMonitor,
    private val crashReporter: CrashReporter
) {

    suspend fun <T> execute(
        apiCall: suspend () -> Response<T>
    ): Resource<T> {

        if (!networkMonitor.isConnected()) {
            return Resource.Error(
                AppError.NoInternet
            )
        }

        return try {

            val response = apiCall()

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.Error(
                        AppError.Unknown
                    )
                }

            } else {

                Resource.Error(
                    response.toAppError()
                )
            }

        } catch (exception: IOException) {

            crashReporter.recordException(
                throwable = exception,
                message = "Network request failed"
            )

            Timber.e(exception, "Network request exception")
            Resource.Error(
                AppError.NetworkRequestFailed
            )

        } catch (exception: Exception) {

            crashReporter.recordException(
                throwable = exception,
                message = "Unexpected API error"
            )

            Timber.e(exception, "Unexpected API error")
            Resource.Error(
                AppError.Unknown
            )
        }
    }

    private fun <T> Response<T>.toAppError(): AppError {

        val errorBody = errorBody()?.string()

        val apiError = apiErrorParser.parse(errorBody)

        return when (code()) {

            401 -> {
                AppError.Unauthorized
            }

            403 -> {
                AppError.Forbidden
            }

            404 -> {
                AppError.NotFound
            }

            in 500..599 -> {
                AppError.Server
            }

            else -> {
                AppError.Api(
                    code = code(),
                    message = apiError?.message
                )
            }
        }
    }
}

inline fun <T, R> Resource<T>.map(
    transform: (T) -> R
): Resource<R> {

    return when (this) {

        is Resource.Success -> {
            Resource.Success(
                transform(data)
            )
        }

        is Resource.Error -> {
            this
        }
    }
}