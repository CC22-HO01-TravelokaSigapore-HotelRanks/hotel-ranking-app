package com.c22ho01.hotelranking.viewmodel.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.review.ReviewData
import com.c22ho01.hotelranking.data.remote.response.review.ReviewResponse
import com.c22ho01.hotelranking.data.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow

class ReviewViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {

    fun getHotelReviews(hotelId: Int): LiveData<Result<ReviewResponse>> {
        return reviewRepository.getHotelReviews(hotelId)
    }

    fun getHotelReviewPaging(hotelId: Int): Flow<PagingData<ReviewData>> {
        return reviewRepository.getHotelReviewPaging(hotelId).cachedIn(viewModelScope)
    }
}