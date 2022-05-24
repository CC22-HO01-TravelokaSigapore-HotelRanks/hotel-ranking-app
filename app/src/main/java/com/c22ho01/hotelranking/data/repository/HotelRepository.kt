package com.c22ho01.hotelranking.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelResponse
import com.c22ho01.hotelranking.data.remote.retrofit.HotelService
import com.c22ho01.hotelranking.utils.wrapEspressoIdlingResource
import com.google.gson.Gson

class HotelRepository(
    private val hotelService: HotelService
) {

    fun getFiveStar(): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.getFiveStar()
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: HotelResponse()))
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        HotelResponse::class.java
                    )
                    emit(Result.Error(errorResponse.message ?: "Error"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun searchHotel(keyword: String): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.searchHotel(keyword)
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: HotelResponse()))
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        HotelResponse::class.java
                    )
                    emit(Result.Error(errorResponse.message ?: "Error"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    companion object {

        @Volatile
        private var instance: HotelRepository? = null

        fun getInstance(hotelService: HotelService): HotelRepository =
            instance ?: synchronized(this) {
                instance ?: HotelRepository(hotelService).also {
                    instance = it
                }
            }
    }
}