package com.c22ho01.hotelranking.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIConfig {
    var AUTH_BASE_URL = "https://test1-ywu6raktuq-uc.a.run.app/"
    private const val HOTEL_BASE_URL = "https://hotel-test-ywu6raktuq-uc.a.run.app/"

    private fun getRetrofit(url: String): Retrofit {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun getAuthAPIService(): AuthService {
        val retrofit = getRetrofit(AUTH_BASE_URL)
        return retrofit.create(AuthService::class.java)
    }

    fun getHotelApiService(): HotelService {
        val retrofit = getRetrofit(HOTEL_BASE_URL)
        return retrofit.create(HotelService::class.java)
    }

    fun getProfileAPIService(): ProfileService {
        val retrofit = getRetrofit(AUTH_BASE_URL)
        return retrofit.create(ProfileService::class.java)
    }
}