package com.c22ho01.hotelranking.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.c22ho01.hotelranking.data.remote.retrofit.APIConfig
import com.c22ho01.hotelranking.data.repository.*

object RepositoryInjection {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    fun provideAuthRepository(): AuthRepository {
        val apiService = APIConfig.getAuthAPIService()
        return AuthRepository.getInstance(apiService)
    }

    fun provideTokenRepository(context: Context): TokenRepository {
        return TokenRepository.getInstance(context.dataStore)
    }

    fun provideProfileRepository(): ProfileRepository {
        val apiService = APIConfig.getProfileAPIService()
        return ProfileRepository.getInstance(apiService)
    }

    fun provideHotelRepository(): HotelRepository {
        val apiService = APIConfig.getHotelApiService()
        return HotelRepository.getInstance(apiService)
    }

    fun provideReviewRepository(): ReviewRepository {
        val apiService = APIConfig.getReviewAPIService()
        return ReviewRepository.getInstance(apiService)
    }
}
