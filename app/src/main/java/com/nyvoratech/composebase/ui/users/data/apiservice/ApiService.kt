package com.nyvoratech.composebase.ui.users.data.apiservice

import com.nyvoratech.composebase.ui.users.data.modeldto.UserDto
import com.nyvoratech.composebase.ui.users.domain.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Sample Retrofit service. Add new endpoints here; keep it purely about
 * HTTP concerns (paths, verbs, DTOs) — mapping/business logic belongs in
 * the data/repository layer.
 */
interface ApiService {

    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>
}