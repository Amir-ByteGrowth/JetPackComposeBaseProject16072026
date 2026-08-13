package com.nyvoratech.composebase.ui.postusers.data.apiservice

import com.nyvoratech.composebase.ui.postusers.data.modeldto.PostUserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/** Retrofit service for the jsonplaceholder /users endpoint. */
interface PostUserApiService {

    @GET("users")
    suspend fun getPostUsers(): Response<List<PostUserDto>>

    @GET("users/{id}")
    suspend fun getPostUserById(@Path("id") id: Long): Response<PostUserDto>
}