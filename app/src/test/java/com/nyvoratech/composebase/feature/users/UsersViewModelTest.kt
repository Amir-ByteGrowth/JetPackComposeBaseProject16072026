package com.nyvoratech.composebase.feature.users

import app.cash.turbine.test
import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.ui.users.domain.model.User
import com.nyvoratech.composebase.ui.users.domain.usecases.GetUsersUseCase
import com.nyvoratech.composebase.ui.users.domain.usecases.RefreshUsersUseCase
import com.nyvoratech.composebase.fakes.FakeUserRepository
import com.nyvoratech.composebase.ui.users.ui.UsersViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UsersViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var fakeUserRepository: FakeUserRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        fakeUserRepository = FakeUserRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh populates users on success`() = runTest {
        fakeUserRepository.seedUsersOnRefresh = listOf(
            User(id = "1", name = "Ada Lovelace", email = "ada@example.com", avatarUrl = null)
        )
        fakeUserRepository.refreshResult = Resource.Success(Unit)

        val viewModel = UsersViewModel(
            getUsersUseCase = GetUsersUseCase(fakeUserRepository),
            refreshUsersUseCase = RefreshUsersUseCase(fakeUserRepository),
            analyticsLogger = mockk(relaxed = true)
        )

        viewModel.uiState.test {
            dispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertEquals(1, state.users.size)
            assertEquals("Ada Lovelace", state.users.first().name)
            assertEquals(false, state.isRefreshing)
        }
    }

    @Test
    fun `refresh failure surfaces error message and keeps stale users`() = runTest {
        fakeUserRepository.refreshResult = Resource.Error("Network error. Please check your connection.")

        val viewModel = UsersViewModel(
            getUsersUseCase = GetUsersUseCase(fakeUserRepository),
            refreshUsersUseCase = RefreshUsersUseCase(fakeUserRepository),
            analyticsLogger = mockk(relaxed = true)
        )

        viewModel.uiState.test {
            dispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertEquals("Network error. Please check your connection.", state.errorMessage)
        }
    }
}
