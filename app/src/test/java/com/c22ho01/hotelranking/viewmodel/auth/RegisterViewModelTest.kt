package com.c22ho01.hotelranking.viewmodel.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.filters.MediumTest
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.auth.RegisterResponse
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
class RegisterViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRulesUnitTest = MainCoroutineRuleUnitTest()

    @Mock
    private lateinit var authRepositoryMock: AuthRepository

    private lateinit var registerViewModel: RegisterViewModel

    private val dummyUsername = "dummyUsername"
    private val dummyEmail = "dummyEmail"
    private val dummyPassword = "dummyPassword"

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(authRepositoryMock)
    }

    @Test
    fun `when all field is valid, should set formValid to true`() {
        registerViewModel.apply {
            setUsernameValid(true)
            setEmailValid(true)
            setPasswordValid(true)
            setConfirmPasswordValid(true)
        }
        val actualFormValid = registerViewModel.formValid.getOrAwaitValue()

        Assert.assertTrue(actualFormValid)
    }

    @Test
    fun `when submitRegister should not return null`() {
        val expectedResult = MutableLiveData<Result<RegisterResponse>>()
        expectedResult.value = Result.Success(RegisterResponse(message = "Success"))

        Mockito.`when`(authRepositoryMock.submitRegister(dummyUsername, dummyEmail, dummyPassword))
            .thenReturn(expectedResult)

        val actualResult =
            registerViewModel.submitRegister(dummyUsername, dummyEmail, dummyPassword)
                .getOrAwaitValue()

        Assert.assertNotNull(actualResult)
        Assert.assertEquals(expectedResult.value, actualResult)
    }
}