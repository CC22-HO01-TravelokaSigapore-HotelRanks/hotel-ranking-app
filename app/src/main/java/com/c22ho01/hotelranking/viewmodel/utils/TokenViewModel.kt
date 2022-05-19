package com.c22ho01.hotelranking.viewmodel.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c22ho01.hotelranking.data.repository.TokenRepository
import kotlinx.coroutines.launch

class TokenViewModel(
    private val tokenRepository: TokenRepository,
) : ViewModel() {
    fun getToken() = tokenRepository.getToken()
    fun setToken(token: String) = viewModelScope.launch { tokenRepository.setToken(token) }
    fun deleteToken() = viewModelScope.launch { tokenRepository.deleteToken() }
}