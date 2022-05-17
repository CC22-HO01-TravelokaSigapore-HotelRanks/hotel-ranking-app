package com.c22ho01.hotelranking.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object RepositoryInjection {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

//    fun provideAuthRepository(): AuthRepository {
//        val apiService = APIConfig.getAuthAPIService()
//        return AuthRepository.getInstance(apiService)
//    }


//    fun provideTokenRepository(context: Context): TokenRepository {
//        return TokenRepository.getInstance(context.dataStore)
//    }

}
