package com.c22ho01.hotelranking.data.remote.retrofit

import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIConfig {
    var BASE_URL = "https://test1-ywu6raktuq-uc.a.run.app/"

    private fun getRetrofit(url: String): Retrofit {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
            }
        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun getAuthAPIService(): AuthService {
        val retrofit = getRetrofit(BASE_URL)
        return retrofit.create(AuthService::class.java)
    }

    fun getHotelApiService(): HotelService {
        val retrofit = getRetrofit(BASE_URL)
        return retrofit.create(HotelService::class.java)
    }

    fun getProfileAPIService(): ProfileService {
        val retrofit = getRetrofit(BASE_URL)
        return retrofit.create(ProfileService::class.java)
    }
}