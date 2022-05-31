package com.c22ho01.hotelranking.viewmodel.hotel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelResponse
import com.c22ho01.hotelranking.data.repository.HotelRepository

class HomeViewModel(hotelRepository: HotelRepository) : ViewModel() {

    val getFiveStar: LiveData<Result<HotelResponse>> = hotelRepository.getFiveStar()

    val getTrending: LiveData<Result<HotelResponse>> = hotelRepository.getTrending()

    val getAll: LiveData<Result<HotelResponse>> = hotelRepository.getAll()

}