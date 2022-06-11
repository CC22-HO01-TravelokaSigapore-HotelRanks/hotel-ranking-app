package com.c22ho01.hotelranking.viewmodel.profile

import androidx.lifecycle.*
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.data.repository.PreferenceRepository
import com.c22ho01.hotelranking.data.repository.ProfileRepository
import com.c22ho01.hotelranking.data.repository.TokenRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val tokenRepository: TokenRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private var _userToken: String = ""
    val userToken: String
        get() = _userToken

    init {
        loadToken()
    }

    private fun loadToken() {
        viewModelScope.launch {
            tokenRepository.getToken().asFlow().collect { token ->
                _userToken = "Bearer $token"
            }
        }
    }

    fun loadProfile() = profileRepository.getProfile(_userToken)
    fun getCurrentProfile() = profileRepository.currentProfile
    fun setCurrentProfile(profile: ProfileEntity) = profileRepository.setCurrentProfile(profile)

    fun setProfileID(profileId: Int) = profileRepository.setProfileId(profileId)
    fun getProfileID() = getCurrentProfile().value?.id

    fun getSavedProfileId() = profileRepository.getSavedProfileId()
    fun setSavedProfileId(id: Int) =
        viewModelScope.launch { profileRepository.setSavedProfileId(id) }

    fun deleteSavedProfileId() = viewModelScope.launch { profileRepository.deleteSavedProfileId() }

    fun getThemeSettings(): LiveData<Boolean> {
        return preferenceRepository.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkMode: Boolean) {
        viewModelScope.launch {
            preferenceRepository.saveThemeSetting(isDarkMode)
        }
    }
}