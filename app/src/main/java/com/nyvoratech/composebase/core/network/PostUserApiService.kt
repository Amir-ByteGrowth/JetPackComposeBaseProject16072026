package com.nyvoratech.composebase.core.network

import com.nyvoratech.composebase.data.remote.dto.PostUserDto
import retrofit2.http.GET
import retrofit2.http.Path

/** Retrofit service for the jsonplaceholder /users endpoint. */
interface PostUserApiService {

    @GET("users")
    suspend fun getPostUsers(): List<PostUserDto>

    @GET("users/{id}")
    suspend fun getPostUserById(@Path("id") id: Long): PostUserDto
}