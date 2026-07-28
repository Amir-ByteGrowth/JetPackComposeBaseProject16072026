package com.nyvoratech.composebase.data.repository

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.core.common.safeCall
import com.nyvoratech.composebase.core.network.PostUserApiService
import com.nyvoratech.composebase.data.mapper.toDomain
import com.nyvoratech.composebase.data.mapper.toPostUserResponse
import com.nyvoratech.composebase.domain.model.PostUser
import com.nyvoratech.composebase.domain.model.PostUserResponse
import com.nyvoratech.composebase.domain.repository.PostUserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostUserRepositoryImpl @Inject constructor(
    private val postUserApiService: PostUserApiService
) : PostUserRepository {

    override suspend fun getPostUsers(): Resource<PostUserResponse> = safeCall {
        postUserApiService.getPostUsers().toPostUserResponse()
    }

    override suspend fun getPostUserById(id: Long): Resource<PostUser> = safeCall {
        postUserApiService.getPostUserById(id).toDomain()
    }
}