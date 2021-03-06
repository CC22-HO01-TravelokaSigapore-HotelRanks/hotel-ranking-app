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

    private fun checkEveryValidationValueTrue(): Boolean {
        return (_usernameValid.value ?: false) && (_passwordValid.value ?: false)
    }

    init {
        formValid.addSource(_usernameValid) {
            formValid.value = checkEveryValidationValueTrue()
        }
        formValid.addSource(_passwordValid) {
            formValid.value = checkEveryValidationValueTrue()
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

    fun submitLoginByGoogle(
        code: String,
    ): LiveData<Result<LoginResponse>> {
        return authRepository.submitLoginByGoogle(code)
    }

    fun submitRefreshLogin(
        refreshToken: String,
    ): LiveData<Result<LoginResponse>> {
        return authRepository.submitRefreshLogin(refreshToken)
    }
}