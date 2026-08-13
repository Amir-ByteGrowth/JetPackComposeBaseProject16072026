package com.nyvoratech.composebase.ui.postusers.data.repositoryimpl

import com.nyvoratech.composebase.core.network.ApiCallHandler
import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.core.network.map
import com.nyvoratech.composebase.domain.model.PostUser
import com.nyvoratech.composebase.ui.postusers.data.apiservice.PostUserApiService
import com.nyvoratech.composebase.ui.postusers.data.mapper.toDomain
import com.nyvoratech.composebase.ui.postusers.data.mapper.toPostUser
import com.nyvoratech.composebase.ui.postusers.domain.repository.PostUserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostUserRepositoryImpl @Inject constructor(
    private val postUserApiService: PostUserApiService,
    private val apiCallHandler: ApiCallHandler
) : PostUserRepository {

    override suspend fun getPostUsers(): Resource<List<PostUser>> =
        apiCallHandler
            .execute {
                postUserApiService.getPostUsers()
            }
            .map { response ->
                response.toDomain()
            }


    override suspend fun getPostUserById(id: Long): Resource<PostUser> =
        apiCallHandler
            .execute {
                postUserApiService.getPostUserById(id)
            }
            .map { response ->
                response.toPostUser()
            }

}