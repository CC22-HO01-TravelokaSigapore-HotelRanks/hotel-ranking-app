package com.c22ho01.hotelranking.viewmodel.hotel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelResponse
import com.c22ho01.hotelranking.data.remote.response.hotel.UserLocation
import com.c22ho01.hotelranking.data.repository.HotelRepository

class HomeViewModel(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    val getFiveStar: LiveData<Result<HotelResponse>> = hotelRepository.getFiveStar()

    val getTrending: LiveData<Result<HotelResponse>> = hotelRepository.getTrending()

    fun getLocation(token: String, userLocation: UserLocation): LiveData<Result<HotelResponse>> {
        return hotelRepository.getNearbyLocation(token, userLocation)
    }

    fun getUserRecommendation(token: String, id: Int): LiveData<Result<HotelResponse>> {
        return hotelRepository.getUserRecommendation(token, id)
    }
}