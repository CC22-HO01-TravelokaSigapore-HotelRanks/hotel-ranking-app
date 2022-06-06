package com.c22ho01.hotelranking.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceRepository private constructor(private val dataStore: DataStore<Preferences>) {

    private val themeSetting = booleanPreferencesKey(THEME)

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map {
            it[themeSetting] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkMode: Boolean) {
        dataStore.edit {
            it[themeSetting] = isDarkMode
        }
    }

    companion object {
        private const val THEME = "theme"

        @Volatile
        private var INSTANCE: PreferenceRepository? = null

        fun getInstance(dataStore: DataStore<Preferences>): PreferenceRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferenceRepository(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}