package com.c22ho01.hotelranking.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.data.remote.retrofit.HotelService

class SearchPagingSource(
    private val hotelService: HotelService,
    private val keyword: String
) : PagingSource<Int, HotelData>() {

    override fun getRefreshKey(state: PagingState<Int, HotelData>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HotelData> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseHotelData = hotelService.searchHotel(params.loadSize, page, keyword)

            LoadResult.Page(
                data = responseHotelData,
                prevKey = null,
                nextKey = if (responseHotelData.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}