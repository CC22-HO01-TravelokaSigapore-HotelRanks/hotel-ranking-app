package com.c22ho01.hotelranking.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.review.ReviewResponse
import com.c22ho01.hotelranking.data.remote.retrofit.ReviewService
import com.c22ho01.hotelranking.utils.wrapEspressoIdlingResource
import com.google.gson.Gson

class ReviewRepository(private val reviewService: ReviewService) {

    fun getHotelReviews(hotelId: Int): LiveData<Result<ReviewResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = reviewService.getHotelReviews(hotelId, 10, 0)
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: ReviewResponse()))
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ReviewResponse::class.java
                    )
                    emit(Result.Error(errorResponse.message ?: "Error"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    companion object {

        @Volatile
        private var instance: ReviewRepository? = null

        fun getInstance(reviewService: ReviewService): ReviewRepository =
            instance ?: synchronized(this) {
                instance ?: ReviewRepository(reviewService).also {
                    instance = it
                }
            }
    }
}