package com.c22ho01.hotelranking.viewmodel.hotel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData

class DetailViewModel : ViewModel() {

    private val _hotelData = MutableLiveData<HotelData>()
    val hotelData: LiveData<HotelData> = _hotelData

    fun setHotel (hotelData: HotelData){
        this._hotelData.value = hotelData
    }

    fun getHotel () = hotelData
}