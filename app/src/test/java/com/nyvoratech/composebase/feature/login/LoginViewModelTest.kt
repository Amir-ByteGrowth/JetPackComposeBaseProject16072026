package com.nyvoratech.composebase.feature.login

import app.cash.turbine.test
import com.nyvoratech.composebase.core.common.FirebaseAnalyticsLogger
import com.nyvoratech.composebase.core.common.Resource
import com.nyvoratech.composebase.ui.login.domain.usecases.LoginUseCase
import com.nyvoratech.composebase.fakes.FakeAuthRepository
import com.nyvoratech.composebase.ui.login.ui.LoginEvent
import com.nyvoratech.composebase.ui.login.ui.LoginViewModel
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        fakeAuthRepository = FakeAuthRepository()
        val loginUseCase = LoginUseCase(fakeAuthRepository)
        val analyticsLogger = mockk<FirebaseAnalyticsLogger>(relaxed = true)
        viewModel = LoginViewModel(loginUseCase, analyticsLogger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invalid email shows validation error without calling repository`() = runTest {
        viewModel.onEmailChanged("not-an-email")
        viewModel.onPasswordChanged("password123")

        viewModel.onLoginClicked()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Please enter a valid email address.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `successful login emits NavigateToUsers event`() = runTest {
        fakeAuthRepository.signInResult = Resource.Success(Unit)
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("password123")

        viewModel.events.test {
            viewModel.onLoginClicked()
            dispatcher.scheduler.advanceUntilIdle()

            assertEquals(LoginEvent.NavigateToUsers, awaitItem())
        }
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `failed login surfaces repository error message`() = runTest {
        fakeAuthRepository.signInResult = Resource.Error("Invalid credentials")
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("password123")

        viewModel.onLoginClicked()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Invalid credentials", viewModel.uiState.value.errorMessage)
    }
}
