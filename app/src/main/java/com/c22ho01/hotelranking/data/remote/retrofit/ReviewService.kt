package com.c22ho01.hotelranking.data.remote.retrofit

import com.c22ho01.hotelranking.data.remote.response.review.PostReviewResponse
import com.c22ho01.hotelranking.data.remote.response.review.ReviewResponse
import retrofit2.Response
import retrofit2.http.*

interface ReviewService {
    @GET("reviews/{hotelId}")
    suspend fun getHotelReviews(
        @Path("hotelId") hotelId: Int,
        @Query("page") page: Int,
        @Query("offset") offset: Int

    ): Response<ReviewResponse>

    @POST("reviews/{hotelId}/{userId}")
    @FormUrlEncoded
    suspend fun postReview(
        @Header("Authorization") token: String,
        @Path("hotelId") hotelId: Int,
        @Path("userId") UserId: Int,
        @Field("text") text: String,
        @Field("rating") rating: Int
    ): Response<PostReviewResponse>
}