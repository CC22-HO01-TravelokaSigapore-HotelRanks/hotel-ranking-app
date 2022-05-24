package com.c22ho01.hotelranking.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenRepository(
    private val settingPreferences: DataStore<Preferences>
) {
    private val tokenPref = stringPreferencesKey(PREF_TOKEN)

    fun getToken(): Flow<String?> = settingPreferences.data.map { it[tokenPref] }

    suspend fun setToken(token: String) {
        settingPreferences.edit { it[tokenPref] = token }
    }

    suspend fun deleteToken() {
        settingPreferences.edit { it.remove(tokenPref) }
    }

    companion object {
        const val PREF_TOKEN = "PREF_TOKEN"

        @Volatile
        private var instance: TokenRepository? = null

        fun getInstance(settingPreferences: DataStore<Preferences>): TokenRepository =
            instance
                ?: synchronized(this) {
                    instance
                        ?: TokenRepository(
                            settingPreferences,
                        )
                            .also { instance = it }
                }
    }
}