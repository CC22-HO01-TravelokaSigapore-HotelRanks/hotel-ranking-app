package com.c22ho01.hotelranking.viewmodel.hotel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelResponse
import com.c22ho01.hotelranking.data.repository.HotelRepository

class HomeViewModel(private val hotelRepository: HotelRepository) : ViewModel() {

    fun getFiveStar(): LiveData<Result<HotelResponse>> {
        return hotelRepository.getFiveStar()
    }

}