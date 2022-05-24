package com.c22ho01.hotelranking.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.data.repository.AuthRepository

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private var _usernameValid = MutableLiveData(false)
    private var _passwordValid = MutableLiveData(false)

    var formValid: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        formValid.addSource(_usernameValid) {
            formValid.value = (_usernameValid.value ?: false) && (_passwordValid.value ?: false)
        }
        formValid.addSource(_passwordValid) {
            formValid.value = (_usernameValid.value ?: false) && (_passwordValid.value ?: false)
        }
    }

    fun setUsernameValid(valid: Boolean) {
        _usernameValid.postValue(valid)
    }

    fun setPasswordValid(valid: Boolean) {
        _passwordValid.postValue(valid)
    }

    fun submitLogin(
        userName: String,
        password: String,
    ): LiveData<Result<LoginResponse>> {
        return authRepository.submitLogin(userName, password)
    }
}