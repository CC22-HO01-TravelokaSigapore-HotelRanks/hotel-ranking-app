package com.c22ho01.hotelranking.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.SearchPagingSource
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelResponse
import com.c22ho01.hotelranking.data.remote.response.hotel.UserLocation
import com.c22ho01.hotelranking.data.remote.retrofit.HotelService
import com.c22ho01.hotelranking.utils.ErrorUtils
import com.c22ho01.hotelranking.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow

class HotelRepository(private val hotelService: HotelService) {

    fun getFiveStar(): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.getFiveStar(10, 0)
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: HotelResponse()))
                } else {
                    val error = ErrorUtils.showErrorFromResponse(response)
                    emit(Result.Error(error))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun getTrending(): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.getTrending(10, 0)
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: HotelResponse()))
                } else {
                    val error = ErrorUtils.showErrorFromResponse(response)
                    emit(Result.Error(error))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun getAll(): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.getAll(10, 0)
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: HotelResponse()))
                } else {
                    val error = ErrorUtils.showErrorFromResponse(response)
                    emit(Result.Error(error))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun getNearbyLocation(userLocation: UserLocation): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.getLocation(userLocation)
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: HotelResponse()))
                } else {
                    val error = ErrorUtils.showErrorFromResponse(response)
                    emit(Result.Error(error))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun searchHotel(keyword: String): Flow<PagingData<HotelData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(hotelService, keyword)
            }
        ).flow
    }

//    fun searchHotel(keyword: String): LiveData<Result<PagingData<HotelData>>> = liveData {
//        Pager(
//            config = PagingConfig(
//                pageSize = 10,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = {
//                SearchPagingSource(hotelService, keyword)
//            }
//        ).liveData
//    }

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