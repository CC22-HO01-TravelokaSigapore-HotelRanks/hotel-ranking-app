package com.c22ho01.hotelranking.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.auth.RegisterResponse
import com.c22ho01.hotelranking.data.repository.AuthRepository

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private var _usernameValid = MutableLiveData(false)
    private var _emailValid = MutableLiveData(false)
    private var _passwordValid = MutableLiveData(false)
    private var _confirmPasswordValid = MutableLiveData(false)

    var formValid: MediatorLiveData<Boolean> = MediatorLiveData()

    private fun checkEveryValidationValueTrue(): Boolean {
        return _usernameValid.value ?: false && _emailValid.value ?: false && _passwordValid.value ?: false && _confirmPasswordValid.value ?: false
    }

    init {
        formValid.addSource(_usernameValid) {
            formValid.value = checkEveryValidationValueTrue()
        }
        formValid.addSource(_emailValid) {
            formValid.value = checkEveryValidationValueTrue()
        }
        formValid.addSource(_passwordValid) {
            formValid.value = checkEveryValidationValueTrue()
        }
        formValid.addSource(_confirmPasswordValid) {
            formValid.value = checkEveryValidationValueTrue()
        }
    }

    fun setUsernameValid(valid: Boolean) {
        _usernameValid.postValue(valid)
    }

    fun setEmailValid(valid: Boolean) {
        _emailValid.postValue(valid)
    }

    fun setPasswordValid(valid: Boolean) {
        _passwordValid.postValue(valid)
    }

    fun setConfirmPasswordValid(valid: Boolean) {
        _confirmPasswordValid.postValue(valid)
    }

    fun submitRegister(
        userName: String,
        email: String,
        password: String,
    ): LiveData<Result<RegisterResponse>> {
        return authRepository.submitRegister(userName, email, password)
    }
}
