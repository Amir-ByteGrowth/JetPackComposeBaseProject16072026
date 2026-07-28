package com.nyvoratech.composebase.domain.usecase

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.model.PostUserResponse
import com.nyvoratech.composebase.domain.repository.PostUserRepository
import javax.inject.Inject

class GetPostUsersUseCase @Inject constructor(
    private val postUserRepository: PostUserRepository
) {
    suspend operator fun invoke(): Resource<PostUserResponse> = postUserRepository.getPostUsers()
}