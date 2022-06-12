package com.c22ho01.hotelranking.viewmodel.hotel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelResponse
import com.c22ho01.hotelranking.data.repository.HotelRepository

class DetailViewModel(private val hotelRepository: HotelRepository) : ViewModel() {

    private val _hotelData = MutableLiveData<HotelData>()
    val hotelData: LiveData<HotelData> = _hotelData

    fun setHotel(hotelData: HotelData) {
        this._hotelData.value = hotelData
    }

    fun getSimilar(token: String, hotelId: Int):LiveData<Result<HotelResponse>>{
        return hotelRepository.getSimilar(token, hotelId)
    }

    fun getHotel() = hotelData
}