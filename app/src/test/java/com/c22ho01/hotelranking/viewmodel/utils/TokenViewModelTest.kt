package com.c22ho01.hotelranking.viewmodel.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.filters.MediumTest
import com.c22ho01.hotelranking.data.repository.TokenRepository
import com.c22ho01.hotelranking.utils.MainCoroutineRuleUnitTest
import com.c22ho01.hotelranking.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
class TokenViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRulesUnitTest = MainCoroutineRuleUnitTest()

    @Mock
    private lateinit var tokenRepositoryMock: TokenRepository

    private lateinit var tokenViewModel: TokenViewModel

    private val dummyToken = "token"

    @Before
    fun setUp() {
        tokenViewModel = TokenViewModel(tokenRepositoryMock)
    }

    @Test
    fun `when get token, should not return nullable value`() {
        `when`(tokenRepositoryMock.getToken()).thenReturn(
            flow { emit(dummyToken) }
        )

        val actualToken = tokenViewModel.getAccessToken().asLiveData().getOrAwaitValue()
        Assert.assertEquals(dummyToken, actualToken)
    }
}