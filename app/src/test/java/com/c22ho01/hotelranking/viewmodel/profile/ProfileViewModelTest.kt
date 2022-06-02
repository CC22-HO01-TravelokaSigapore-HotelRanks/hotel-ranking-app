package com.c22ho01.hotelranking.viewmodel.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.filters.MediumTest
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.data.repository.ProfileRepository
import com.c22ho01.hotelranking.data.repository.TokenRepository
import com.c22ho01.hotelranking.utils.DataDummy
import com.c22ho01.hotelranking.utils.MainCoroutineRuleUnitTest
import com.c22ho01.hotelranking.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRulesUnitTest = MainCoroutineRuleUnitTest()

    @Mock
    private lateinit var profileRepositoryMock: ProfileRepository

    @Mock
    private lateinit var tokenRepositoryMock: TokenRepository

    private lateinit var profileViewModel: ProfileViewModel

    private val dummyToken = "dummyToken"
    private val dummyBearerToken = "Bearer $dummyToken"

    @Before
    fun setUp() {
        `when`(tokenRepositoryMock.getToken()).thenReturn(flow { emit(dummyToken) })
        profileViewModel = ProfileViewModel(profileRepositoryMock, tokenRepositoryMock)
    }

    @Test
    fun `when ProfileViewModel is initialized, set userToken from TokenRepository`() =
        mainCoroutineRulesUnitTest.scope.runTest {
            Assert.assertEquals(dummyBearerToken, profileViewModel.userToken)
        }

    @Test
    fun `when loadProfile should return ProfileEntity`() {
        val expectedResult = MutableLiveData<Result<ProfileEntity>>()
        expectedResult.value = Result.Success(DataDummy.provideProfileEntity())
        `when`(profileRepositoryMock.getProfile(dummyBearerToken)).thenReturn(
            expectedResult
        )

        val actualResult = profileViewModel.loadProfile().getOrAwaitValue()
        Assert.assertEquals(expectedResult.value, actualResult)
    }

    @Test
    fun `when getCurrentProfile should return ProfileEntity`() {
        val expectedResult = MutableLiveData<ProfileEntity>()
        expectedResult.value = DataDummy.provideProfileEntity()
        `when`(profileRepositoryMock.currentProfile).thenReturn(
            expectedResult
        )

        val actualResult = profileViewModel.getCurrentProfile().getOrAwaitValue()
        Assert.assertEquals(expectedResult.value, actualResult)
    }

    @Test
    fun `when getProfileId should return current profile id in repository`() {
        val profileDummy = DataDummy.provideProfileEntity()
        val expectedResult = MutableLiveData<Int>()
        expectedResult.value = profileDummy.id

        `when`(profileRepositoryMock.currentProfile).thenReturn(
            MutableLiveData(profileDummy)
        )

        val actualResult = profileViewModel.getProfileID()
        Assert.assertEquals(expectedResult.value, actualResult)
    }

}