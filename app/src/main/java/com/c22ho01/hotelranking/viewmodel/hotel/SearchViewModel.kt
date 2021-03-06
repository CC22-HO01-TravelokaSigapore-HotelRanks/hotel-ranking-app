package com.c22ho01.hotelranking.viewmodel.hotel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.data.repository.HotelRepository
import kotlinx.coroutines.flow.Flow

class SearchViewModel(private val hotelRepository: HotelRepository) : ViewModel() {

    fun searchHotel(keyword: String): Flow<PagingData<HotelData>> {
        return hotelRepository.searchHotel(keyword).cachedIn(viewModelScope)
    }
}