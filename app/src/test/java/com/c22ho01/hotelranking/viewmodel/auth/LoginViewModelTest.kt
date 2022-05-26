package com.c22ho01.hotelranking.viewmodel.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.filters.MediumTest
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.auth.LoginData
import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.data.repository.AuthRepository
import com.c22ho01.hotelranking.utils.MainCoroutineRuleUnitTest
import com.c22ho01.hotelranking.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRulesUnitTest = MainCoroutineRuleUnitTest()

    @Mock
    private lateinit var authRepositoryMock: AuthRepository

    private lateinit var loginViewModel: LoginViewModel

    private val dummyEmail = "dummyEmail"
    private val dummyPassword = "dummyPassword"

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(authRepositoryMock)
    }

    @Test
    fun `when all fields is valid, should set formValid to true`() {
        loginViewModel.apply {
            setUsernameValid(true)
            setPasswordValid(true)
        }
        val actualFormValid = loginViewModel.formValid.getOrAwaitValue()

        Assert.assertTrue(actualFormValid)
    }

    @Test
    fun `when submitLogin should not return null`() {
        val loginData = LoginData(
            userId = 1,
            accessToken = "dummyToken",
        )
        val loginResponse = LoginResponse(
            loginData = loginData,
            message = "dummyMessage",
            status = "dummyStatus"
        )
        val expectedResult = MutableLiveData<Result<LoginResponse>>()
        expectedResult.value = Result.Success(loginResponse)

        Mockito.`when`(authRepositoryMock.submitLogin(dummyEmail, dummyPassword))
            .thenReturn(expectedResult)

        val actualResult = loginViewModel.submitLogin(dummyEmail, dummyPassword).getOrAwaitValue()

        Assert.assertNotNull(actualResult)
        Assert.assertEquals(expectedResult.value, actualResult)
    }
}