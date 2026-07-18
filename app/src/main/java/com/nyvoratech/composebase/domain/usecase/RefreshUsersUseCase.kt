package com.nyvoratech.composebase.domain.usecase

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.repository.UserRepository
import javax.inject.Inject

class RefreshUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<Unit> = userRepository.refreshUsers()
}
