package com.nyvoratech.composebase.ui.postusers.domain.usecases

import com.nyvoratech.composebase.core.network.Resource
import com.nyvoratech.composebase.domain.model.PostUser
import com.nyvoratech.composebase.ui.postusers.domain.repository.PostUserRepository
import javax.inject.Inject

class GetPostUserByIdUseCase @Inject constructor(
    private val postUserRepository: PostUserRepository
) {
    suspend operator fun invoke(id: Long): Resource<PostUser> =
        postUserRepository.getPostUserById(id)
}