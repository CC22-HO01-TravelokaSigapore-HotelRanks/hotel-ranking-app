package com.c22ho01.hotelranking.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.IdlingRegistry
import androidx.test.filters.MediumTest
import com.c22ho01.hotelranking.MainCoroutineRule
import com.c22ho01.hotelranking.captureValues
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.auth.RegisterResponse
import com.c22ho01.hotelranking.data.remote.retrofit.AuthService
import com.c22ho01.hotelranking.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule(UnconfinedTestDispatcher())

    @Mock
    private lateinit var authService: AuthService

    private val dummyUserName = "dummyUserName"
    private val dummyEmail = "dummyEmail"
    private val dummyPassword = "dummyPassword"

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        authRepository = AuthRepository(authService)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun `when submitRegister should not return null`() =
        mainCoroutineRules.scope.runTest {
            val dummyResponse =
                RegisterResponse(
                    message = "success",
                )
            val expectedResult = Result.Success(dummyResponse)
            Mockito.`when`(authService.register(dummyUserName, dummyEmail, dummyPassword))
                .thenReturn(
                    Response.success(dummyResponse)
                )
            authRepository.submitRegister(dummyUserName, dummyEmail, dummyPassword).captureValues {
                Assert.assertNotNull(values)
                Assert.assertEquals(arrayListOf(Result.Loading, expectedResult), values)
            }
        }

}