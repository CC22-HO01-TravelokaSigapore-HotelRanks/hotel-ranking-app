package com.c22ho01.hotelranking.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.ReviewPagingSource
import com.c22ho01.hotelranking.data.remote.response.review.PostReviewResponse
import com.c22ho01.hotelranking.data.remote.response.review.ReviewData
import com.c22ho01.hotelranking.data.remote.response.review.ReviewResponse
import com.c22ho01.hotelranking.data.remote.retrofit.ReviewService
import com.c22ho01.hotelranking.utils.wrapEspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

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

    fun postReview(
        token: String,
        hotelId: Int,
        userId: Int,
        text: String,
        rating: Int
    ): LiveData<Result<PostReviewResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = reviewService.postReview(token, hotelId, userId, text, rating)
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: PostReviewResponse()))
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        PostReviewResponse::class.java
                    )
                    emit(Result.Error(errorResponse.message ?: "Error"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun getHotelReviewPaging(hotelId: Int): Flow<PagingData<ReviewData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ReviewPagingSource(reviewService, hotelId)
            }
        ).flow
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