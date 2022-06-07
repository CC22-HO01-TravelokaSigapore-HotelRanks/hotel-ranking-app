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

    fun getNearbyLocation(
        token: String,
        userLocation: UserLocation
    ): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.getLocation(token, userLocation)
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

    fun getForYou(
        userToken: String,
        userLocation: UserLocation
    ): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.getForYou(userToken, userLocation)
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

    fun getUserRecommendation(token: String, id: Int): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.getUserRecommendation(token, id)
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

    fun getSimilar(token: String, hotelId: Int): LiveData<Result<HotelResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = hotelService.getSimilar(token, hotelId)
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