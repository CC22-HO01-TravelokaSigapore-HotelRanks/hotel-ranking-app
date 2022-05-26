package com.c22ho01.hotelranking.viewmodel.hotel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelResponse
import com.c22ho01.hotelranking.data.repository.HotelRepository

class SearchViewModel(private val hotelRepository: HotelRepository) : ViewModel() {

    fun searchHotel(keyword: String): LiveData<PagingData<HotelData>> {
        return hotelRepository.searchHotel(keyword).cachedIn(viewModelScope)
    }

    fun hotelSearch(keyword: String): LiveData<Result<HotelResponse>> {
        return hotelRepository.hotelSearch(keyword)
    }
}