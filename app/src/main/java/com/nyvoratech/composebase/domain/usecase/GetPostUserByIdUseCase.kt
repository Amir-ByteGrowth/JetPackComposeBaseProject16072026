package com.nyvoratech.composebase.domain.usecase

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.model.PostUser
import com.nyvoratech.composebase.domain.repository.PostUserRepository
import javax.inject.Inject

class GetPostUserByIdUseCase @Inject constructor(
    private val postUserRepository: PostUserRepository
) {
    suspend operator fun invoke(id: Long): Resource<PostUser> = postUserRepository.getPostUserById(id)
}