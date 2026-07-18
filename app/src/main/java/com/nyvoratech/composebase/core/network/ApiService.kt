package com.nyvoratech.composebase.core.network

import com.nyvoratech.composebase.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Sample Retrofit service. Add new endpoints here; keep it purely about
 * HTTP concerns (paths, verbs, DTOs) — mapping/business logic belongs in
 * the data/repository layer.
 */
interface ApiService {

    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserDto
}
