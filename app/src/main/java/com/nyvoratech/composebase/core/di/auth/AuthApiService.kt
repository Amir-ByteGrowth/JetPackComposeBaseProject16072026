package com.nyvoratech.composebase.core.di.auth

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse

}