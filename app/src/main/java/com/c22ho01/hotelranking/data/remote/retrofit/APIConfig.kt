package com.c22ho01.hotelranking.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIConfig {
    var BASE_URL = "https://hotel-recommendation-system-18j6qgxy.uc.gateway.dev/"

    private fun getRetrofit(): Retrofit {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun getAuthAPIService(): AuthService {
        val retrofit = getRetrofit()
        return retrofit.create(AuthService::class.java)
    }

    fun getHotelApiService(): HotelService {
        val retrofit = getRetrofit()
        return retrofit.create(HotelService::class.java)
    }

    fun getProfileAPIService(): ProfileService {
        val retrofit = getRetrofit()
        return retrofit.create(ProfileService::class.java)
    }

    fun getReviewAPIService(): ReviewService {
        val retrofit = getRetrofit()
        return retrofit.create(ReviewService::class.java)
    }
}