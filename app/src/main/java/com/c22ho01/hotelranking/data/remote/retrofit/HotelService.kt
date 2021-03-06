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

    @GET("trending")
    suspend fun getTrending(
        @Query("page") page: Int,
        @Query("offset") offset: Int
    ): Response<HotelResponse>

    @POST("by-location")
    suspend fun getLocation(
        @Header("Authorization") token: String,
        @Body location: UserLocation
    ): Response<HotelResponse>

    @POST("user-recommendation/{id}")
    suspend fun getUserRecommendation(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<HotelResponse>

    @POST("hotel-recommendation/{hotelId}")
    suspend fun getSimilar(
        @Header("Authorization") token: String,
        @Path("hotelId") hotelId: Int
    ): Response<HotelResponse>

    @POST("for-you")
    suspend fun getForYou(
        @Header("Authorization") token: String,
        @Body location: UserLocation
    ): Response<HotelResponse>
}