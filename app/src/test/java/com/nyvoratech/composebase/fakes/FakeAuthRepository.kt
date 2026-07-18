package com.nyvoratech.composebase.fakes

import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * In-memory fake used by ViewModel/UseCase unit tests instead of a mock
 * framework — cheap to reason about and doesn't couple tests to
 * Firebase/Mockk call syntax.
 */
class FakeAuthRepository : AuthRepository {

    var signInResult: Resource<Unit> = Resource.Success(Unit)
    private val authState = MutableStateFlow<String?>(null)

    override fun observeAuthState(): StateFlow<String?> = authState

    override suspend fun signIn(email: String, password: String): Resource<Unit> {
        if (signInResult is Resource.Success) {
            authState.value = "fake-uid"
        }
        return signInResult
    }

    override suspend fun signUp(email: String, password: String): Resource<Unit> = signInResult

    override fun signOut() {
        authState.value = null
    }
}
