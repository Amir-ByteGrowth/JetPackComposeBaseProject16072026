package com.nyvoratech.composebase.ui.postusers.domain.repository

import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.domain.model.PostUser

interface PostUserRepository {
    suspend fun getPostUsers(): Resource<List<PostUser>>
    suspend fun getPostUserById(id: Long): Resource<PostUser>
}