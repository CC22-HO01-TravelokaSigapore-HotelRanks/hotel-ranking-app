package com.c22ho01.hotelranking.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.IdlingRegistry
import androidx.test.filters.MediumTest
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.profile.ProfilePutResponse
import com.c22ho01.hotelranking.data.remote.retrofit.ProfileService
import com.c22ho01.hotelranking.utils.*
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

    private val dummyToken = "dummyToken"
    private val dummyUserId = 1

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        profileRepository = ProfileRepository(profileService)
        profileRepository.setProfileId(dummyUserId)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun `when getProfile should not return null`() =
        mainCoroutineRulesUnitTest.scope.runTest {
            val dummyResponse = DataDummy.provideProfileResponse()
            val expectedResult = Result.Success(
                DataDummy.provideProfileEntity()
            )

            Mockito.`when`(profileService.getUserById(dummyToken, 1)).thenReturn(
                Response.success(dummyResponse)
            )
            profileRepository.getProfile(dummyToken).captureValues {
                Assert.assertNotNull(values)
                Assert.assertEquals(arrayListOf(Result.Loading, expectedResult), values)
            }
        }

    @Test
    fun `when setProfileId should change currentProfileId`() =
        mainCoroutineRulesUnitTest.scope.runTest {
            val expectedId = 101
            val beforeId = profileRepository.currentProfile.getOrAwaitValue().id
            profileRepository.setProfileId(expectedId)

            val actualId = profileRepository.currentProfile.getOrAwaitValue().id
            Assert.assertNotEquals(beforeId, actualId)
            Assert.assertEquals(expectedId, actualId)
        }

    @Test
    fun `when updateProfile should change currentProfile and not return null`() =
        mainCoroutineRulesUnitTest.scope.runTest {
            val oldProfileEntity = DataDummy.provideProfileEntity()
            val oldCurrentProfile = profileRepository.currentProfile.getOrAwaitValue()
            val newProfileEntity = oldProfileEntity.copy(
                name = "dummyName",
                birthDate = DateUtils.parseDateFromString("2020-01-01"),
                hobby = arrayListOf(
                    profileRepository.hobbyList[2],
                    profileRepository.hobbyList[3],
                ),
            )
            val expectedResponse = ProfilePutResponse(message = "success" )

            Mockito.`when`(
                profileService.updateUserById(
                    dummyToken,
                    id = dummyUserId,
                    name = newProfileEntity.name,
                    birthDate = DateUtils.formatDateToStringDash(newProfileEntity.birthDate!!),
                    nid = newProfileEntity.nid,
                    family = newProfileEntity.family,
                    hobby = newProfileEntity.hobby?.map { it?.fromResponseLabel }?.toList()
                        ?.joinToString(),
                    specialNeeds = newProfileEntity.specialNeeds?.map { it?.fromResponseLabel }
                        ?.toList()
                        ?.joinToString(),
                    searchHistory = newProfileEntity.searchHistory?.joinToString(","),
                    stayHistory = newProfileEntity.stayHistory?.joinToString(","),
                )
            ).thenReturn(
                Response.success(expectedResponse)
            )

            profileRepository.updateProfile(
                dummyToken,
                newProfileEntity
            ).captureValues {
                Assert.assertNotNull(values)
                Assert.assertEquals(arrayListOf(Result.Loading, Result.Success(newProfileEntity)), values)
            }
            val newCurrentProfile = profileRepository.currentProfile.getOrAwaitValue()
            Assert.assertNotEquals(newCurrentProfile, oldCurrentProfile)
            Assert.assertEquals(newCurrentProfile, newProfileEntity)
        }
}