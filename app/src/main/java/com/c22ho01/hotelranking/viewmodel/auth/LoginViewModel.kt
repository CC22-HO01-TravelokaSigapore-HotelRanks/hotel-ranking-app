package com.c22ho01.hotelranking.viewmodel.auth

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22ho01.hotelranking.data.repository.AuthRepository

class LoginViewModel(
    authRepository: AuthRepository,
) : ViewModel() {
    private var _emailValid = MutableLiveData(false)
    private var _passwordValid = MutableLiveData(false)

    var formValid: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        formValid.addSource(_emailValid) {
            formValid.value = (_emailValid.value ?: false) && (_passwordValid.value ?: false)
        }
        formValid.addSource(_passwordValid) {
            formValid.value = (_emailValid.value ?: false) && (_passwordValid.value ?: false)
        }
    }

    fun setEmailValid(valid: Boolean) {
        _emailValid.postValue(valid)
    }
    fun setPasswordValid(valid: Boolean) {
        _passwordValid.postValue(valid)
    }
}