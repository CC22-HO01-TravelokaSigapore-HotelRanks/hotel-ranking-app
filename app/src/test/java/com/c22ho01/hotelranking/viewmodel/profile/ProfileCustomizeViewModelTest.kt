package com.c22ho01.hotelranking.viewmodel.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.c22ho01.hotelranking.data.repository.ProfileRepository
import com.c22ho01.hotelranking.utils.DataDummy
import com.c22ho01.hotelranking.utils.MainCoroutineRuleUnitTest
import com.c22ho01.hotelranking.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

}