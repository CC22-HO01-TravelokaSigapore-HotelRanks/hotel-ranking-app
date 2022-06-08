package com.c22ho01.hotelranking.viewmodel.hotel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.c22ho01.hotelranking.data.remote.response.hotel.UserLocation
import com.c22ho01.hotelranking.data.repository.HotelRepository
import com.c22ho01.hotelranking.data.repository.TokenRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ForYouViewModel(
    private val tokenRepository: TokenRepository,
    private val hotelRepository: HotelRepository,
) : ViewModel() {
    private var _userToken: String = ""
    val userToken: String get() = _userToken

    init {
        loadToken()
    }

    fun loadToken() {
        viewModelScope.launch {
            tokenRepository.getToken().asFlow().collect { token ->
                _userToken = "Bearer $token"
            }
        }
    }

    fun getForYouHotels(userLocation: UserLocation) =
        hotelRepository.getForYou(userToken, userLocation)
}