package com.c22ho01.hotelranking.data.remote.retrofit

import com.c22ho01.hotelranking.data.remote.response.review.ReviewResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewService {
    @GET("reviews/{hotelId}")
    suspend fun getHotelReviews(
        @Path("hotelId") hotelId: Int,
        @Query("page") page: Int,
        @Query("offset") offset: Int

    ): Response<ReviewResponse>

//    @GET("reviews/{hotelId}")
//    suspend fun getHotelReviewsPaging(
//        @Path("hotelId") hotelId: Int,
//        @Query("page") page: Int,
//        @Query("offset") offset: Int
//
//    ): Response<ReviewResponse>
}