package com.nyvoratech.composebase.domain.repository

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.model.PostUser
import com.nyvoratech.composebase.domain.model.PostUserResponse

interface PostUserRepository {
    suspend fun getPostUsers(): Resource<PostUserResponse>
    suspend fun getPostUserById(id: Long): Resource<PostUser>
}