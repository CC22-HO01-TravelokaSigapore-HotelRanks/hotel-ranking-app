package com.c22ho01.hotelranking.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.c22ho01.hotelranking.data.remote.response.review.ReviewData
import com.c22ho01.hotelranking.data.remote.retrofit.ReviewService

class ReviewPagingSource(private val reviewService: ReviewService, private val hotelId: Int) :
    PagingSource<Int, ReviewData>() {
    override fun getRefreshKey(state: PagingState<Int, ReviewData>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReviewData> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseReviewData = reviewService.getHotelReviews(hotelId, params.loadSize, position)

            if (responseReviewData.isSuccessful){
                val reviews = responseReviewData.body()?.results ?: emptyList()
                LoadResult.Page(
                    data = reviews,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (reviews.isNullOrEmpty()) null else position + 1
                )
            }else{
                LoadResult.Error(Exception(responseReviewData.message()))
            }
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}