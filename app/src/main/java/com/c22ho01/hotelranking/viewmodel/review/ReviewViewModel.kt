package com.c22ho01.hotelranking.viewmodel.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.review.ReviewResponse
import com.c22ho01.hotelranking.data.repository.ReviewRepository

class ReviewViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {

    fun getHotelReviews(hotelId: Int): LiveData<Result<ReviewResponse>> {
        return reviewRepository.getHotelReviews(hotelId)
    }
}