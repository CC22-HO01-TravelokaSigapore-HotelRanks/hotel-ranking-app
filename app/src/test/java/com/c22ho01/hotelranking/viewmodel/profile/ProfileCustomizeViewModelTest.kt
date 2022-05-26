package com.c22ho01.hotelranking.viewmodel.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.filters.MediumTest
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.data.repository.ProfileRepository
import com.c22ho01.hotelranking.utils.DataDummy
import com.c22ho01.hotelranking.utils.MainCoroutineRuleUnitTest
import com.c22ho01.hotelranking.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ProfileCustomizeViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRulesUnitTest = MainCoroutineRuleUnitTest()

    @Mock
    private lateinit var profileRepositoryMock: ProfileRepository

    private lateinit var profileCustomizeViewModel: ProfileCustomizeViewModel
    private val dummyToken = "dummyToken"

    @Before
    fun setUp() {
        profileCustomizeViewModel = ProfileCustomizeViewModel(profileRepositoryMock)
        `when`(profileRepositoryMock.hobbyList).thenReturn(DataDummy.provideHobbyList())
        `when`(profileRepositoryMock.disabilityList).thenReturn(DataDummy.provideDisabilityList())
    }

    @Test
    fun `when all fields is valid, should set formValid to true`() {
        profileCustomizeViewModel.apply {
            setFullNameValid(true)
            setNidValid(true)
            setBirthDateValid(true)
            setFamilyValid(true)
            setHobbyChecked(DataDummy.provideHobbyList()[0], true)
            setDisabilityChecked(DataDummy.provideDisabilityList()[0], true)
        }

        val actualFormValid = profileCustomizeViewModel.formValid.getOrAwaitValue()
        Assert.assertTrue(actualFormValid)
    }

    @Test
    fun `when getAllHobbyList should return hobby list`() {
        val actualHobbyList = profileCustomizeViewModel.getAllHobbyList()
        Assert.assertEquals(DataDummy.provideHobbyList(), actualHobbyList)
    }

    @Test
    fun `when getAllDisabilityList should return disability list`() {
        val actualDisabilityList = profileCustomizeViewModel.getAllDisabilityList()
        Assert.assertEquals(DataDummy.provideDisabilityList(), actualDisabilityList)
    }

    @Test
    fun `when customizeProfile should insert all fields value and not return null`() = mainCoroutineRulesUnitTest.scope.runTest {
        val profileEntity = DataDummy.provideProfileEntity().copy(
            hobby = DataDummy.provideHobbyList(),
            specialNeeds = DataDummy.provideDisabilityList(),
            userName = null,
            email = null,
            searchHistory = null,
            stayHistory = null,
            createdAt = null,
            updatedAt = null,
        )
        profileCustomizeViewModel.apply {
            setFullName(profileEntity.name!!)
            setNid(profileEntity.nid!!)
            setBirthDate(profileEntity.birthDate!!)
            setFamily(profileEntity.family!!)
            setSelectedHobbies(profileEntity.hobby!!)
            setSelectedDisabilities(profileEntity.specialNeeds!!)
        }

        val expectedResult = MutableLiveData<Result<ProfileEntity>>()
        expectedResult.value = Result.Success(
            profileEntity
        )

        `when`(profileRepositoryMock.updateProfile(dummyToken, profileEntity)).thenReturn(
            expectedResult
        )

        val actualResult = profileCustomizeViewModel.customizeProfile(dummyToken, profileEntity.id!!)

        Assert.assertNotNull(actualResult)
        Assert.assertEquals(expectedResult, actualResult)
    }

}