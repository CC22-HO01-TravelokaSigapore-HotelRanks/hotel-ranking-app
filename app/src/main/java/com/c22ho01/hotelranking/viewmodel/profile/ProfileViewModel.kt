package com.c22ho01.hotelranking.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c22ho01.hotelranking.data.repository.ProfileRepository
import com.c22ho01.hotelranking.data.repository.TokenRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val tokenRepository: TokenRepository,
) : ViewModel() {

    private var _userToken: String = ""
    val userToken: String
        get() = _userToken

    init {
        loadToken()
    }

    fun loadToken() {
        viewModelScope.launch {
            tokenRepository.getToken().collect { token ->
                _userToken = "Bearer $token"
            }
        }
    }

    fun loadProfile() = profileRepository.getProfile(_userToken)
    fun getCurrentProfile() = profileRepository.currentProfile
    fun setProfileID(profileId: Int) = profileRepository.setProfileId(profileId)
    fun getProfileID() = getCurrentProfile().value?.id
}