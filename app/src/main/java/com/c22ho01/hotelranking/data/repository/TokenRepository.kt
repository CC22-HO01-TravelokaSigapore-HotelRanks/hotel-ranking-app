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
    private val accessTokenPref = stringPreferencesKey(PREF_ACCESS_TOKEN)
    private val refreshTokenPref = stringPreferencesKey(PREF_REFRESH_TOKEN)

    private fun accessedPref(tokenType: String): Preferences.Key<String> {
        return when (tokenType) {
            PREF_ACCESS_TOKEN -> accessTokenPref
            PREF_REFRESH_TOKEN -> refreshTokenPref
            else -> stringPreferencesKey(DEFAULT_ACCESSED_PREF)
        }
    }

    fun getToken(
        tokenType: String = DEFAULT_ACCESSED_PREF
    ): Flow<String?> {
        val accessedPref = accessedPref(tokenType)
        return settingPreferences.data.map { it[accessedPref] }
    }

    suspend fun setToken(
        token: String,
        tokenType: String = DEFAULT_ACCESSED_PREF,
    ) {
        val accessedPref = accessedPref(tokenType)
        settingPreferences.edit { it[accessedPref] = token }
    }

    suspend fun deleteToken(
        tokenType: String = DEFAULT_ACCESSED_PREF
    ) {
        val accessedPref = accessedPref(tokenType)
        settingPreferences.edit { it.remove(accessedPref) }
    }

    companion object {
        const val PREF_ACCESS_TOKEN = "PREF_ACCESS_TOKEN"
        const val PREF_REFRESH_TOKEN = "PREF_REFRESH_TOKEN"
        const val DEFAULT_ACCESSED_PREF = PREF_ACCESS_TOKEN

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