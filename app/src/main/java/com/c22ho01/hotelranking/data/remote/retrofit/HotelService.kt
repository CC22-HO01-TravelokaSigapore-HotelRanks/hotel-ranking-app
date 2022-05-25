package com.c22ho01.hotelranking.data.remote.retrofit

import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HotelService {

    @GET("hotel/5")
    suspend fun getFiveStar(
        @Query("page") page: Int,
        @Query("offset") offset: Int
    ): Response<HotelResponse>

    @GET("search")
    suspend fun searchHotel(
        @Query("page") page: Int,
        @Query("offset") offset: Int,
        @Query("keyword") keyword: String
    ): List<HotelData>
}