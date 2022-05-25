package com.c22ho01.hotelranking.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.IdlingRegistry
import androidx.test.filters.MediumTest
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.retrofit.ProfileService
import com.c22ho01.hotelranking.utils.DataDummy
import com.c22ho01.hotelranking.utils.EspressoIdlingResource
import com.c22ho01.hotelranking.utils.MainCoroutineRuleUnitTest
import com.c22ho01.hotelranking.utils.captureValues
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
class ProfileRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRulesUnitTest = MainCoroutineRuleUnitTest(UnconfinedTestDispatcher())

    @Mock
    private lateinit var profileService: ProfileService

    private lateinit var profileRepository: ProfileRepository

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        profileRepository = ProfileRepository(profileService)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    //wip
    @Test
    fun `when getProfile should not return null`() =
        mainCoroutineRulesUnitTest.scope.runTest {
            val dummyResponse = DataDummy.provideProfileResponse()
            val expectedResult = Result.Success(
                DataDummy.provideProfileEntity()
            )

            Mockito.`when`(profileService.getUserById(1)).thenReturn(
                Response.success(dummyResponse)
            )
            profileRepository.getProfile(1).captureValues {
                Assert.assertNotNull(values)
                Assert.assertEquals(arrayListOf(Result.Loading, expectedResult), values)
            }
        }
}