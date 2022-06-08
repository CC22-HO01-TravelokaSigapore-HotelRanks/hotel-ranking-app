package com.c22ho01.hotelranking.viewmodel.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c22ho01.hotelranking.data.repository.TokenRepository
import kotlinx.coroutines.launch

class TokenViewModel(
    private val tokenRepository: TokenRepository,
) : ViewModel() {
    fun getAccessToken() = tokenRepository.getToken(TokenRepository.PREF_ACCESS_TOKEN)
    fun getRefreshToken() = tokenRepository.getToken(TokenRepository.PREF_REFRESH_TOKEN)

    fun setAccessToken(token: String) = viewModelScope.launch {
        tokenRepository.setToken(token, TokenRepository.PREF_ACCESS_TOKEN)
    }

    fun setRefreshToken(token: String) = viewModelScope.launch {
        tokenRepository.setToken(token, TokenRepository.PREF_REFRESH_TOKEN)
    }

    fun deleteAccessToken() = viewModelScope.launch {
        tokenRepository.deleteToken(TokenRepository.PREF_ACCESS_TOKEN)
    }

    fun deleteRefreshToken() = viewModelScope.launch {
        tokenRepository.deleteToken(TokenRepository.PREF_REFRESH_TOKEN)
    }
}