package com.c22ho01.hotelranking.data.remote.retrofit

import com.c22ho01.hotelranking.data.remote.response.hotel.HotelResponse
import com.c22ho01.hotelranking.data.remote.response.hotel.UserLocation
import retrofit2.Response
import retrofit2.http.*


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
    ): Response<HotelResponse>

    @GET("search")
    suspend fun hotelSearch(
        @Query("offset") offset: Int,
        @Query("keyword") keyword: String
    ): Response<HotelResponse>

    @GET("trending")
    suspend fun getTrending(
        @Query("page") page: Int,
        @Query("offset") offset: Int
    ): Response<HotelResponse>

    @GET("hotel/list")
    suspend fun getAll(
        @Query("page") page: Int,
        @Query("offset") offset: Int
    ): Response<HotelResponse>

    @POST("byLocation")
    suspend fun getLocation(
        @Body location: UserLocation
    ): Response<HotelResponse>

    @POST("for-you")
    suspend fun getForYou(
        @Header("Authorization") token: String,
        @Body location: UserLocation
    ): Response<HotelResponse>
}