package com.c22ho01.hotelranking.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.asLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.c22ho01.hotelranking.utils.MainCoroutineRuleInstTest
import com.c22ho01.hotelranking.utils.getOrAwaitValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@MediumTest
@RunWith(AndroidJUnit4::class)
class TokenRepositoryTest {
    private val testContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRulesInstTest = MainCoroutineRuleInstTest()

    private lateinit var preferencesScope: CoroutineScope
    private lateinit var testDataStore: DataStore<Preferences>

    private lateinit var tokenRepository: TokenRepository
    private val tokenPreference = stringPreferencesKey(TokenRepository.PREF_TOKEN)
    private val testToken = "testToken"

    @Before
    fun setUp() {
        preferencesScope = CoroutineScope(mainCoroutineRulesInstTest.dispatcher + Job())
        testDataStore =
            PreferenceDataStoreFactory.create(
                scope = preferencesScope,
                produceFile = { testContext.preferencesDataStoreFile(TEST_DATASTORE_NAME) },
            )
        tokenRepository = TokenRepository(testDataStore)
    }

    @After
    fun removeDatastore() {
        File(ApplicationProvider.getApplicationContext<Context>().filesDir, "datastore")
            .deleteRecursively()
        preferencesScope.cancel()
    }

    @Test
    fun getToken_shouldReturnToken() =
        mainCoroutineRulesInstTest.scope.runTest {
            testDataStore.edit { it[tokenPreference] = testToken }
            val actualToken = tokenRepository.getToken().asLiveData().getOrAwaitValue()
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(testToken, actualToken)
        }

    @Test
    fun setToken_shouldEditTokenToANewToken() =
        mainCoroutineRulesInstTest.scope.runTest {
            testDataStore.edit { it[tokenPreference] = testToken }
            val newToken = "newToken"
            tokenRepository.setToken(newToken)

            val actualToken =
                testDataStore.data.map { it[tokenPreference] }.asLiveData().getOrAwaitValue()
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(newToken, actualToken)
        }

    @Test
    fun deleteToken_shouldRemoveTokenFromDataStore() =
        mainCoroutineRulesInstTest.scope.runTest {
            testDataStore.edit { it[tokenPreference] = testToken }
            tokenRepository.deleteToken()

            val actualToken =
                testDataStore.data.map { it[tokenPreference] }.asLiveData().getOrAwaitValue()
            Assert.assertNull(actualToken)
        }

    companion object {
        private const val TEST_DATASTORE_NAME = "test_datastore"
    }
}