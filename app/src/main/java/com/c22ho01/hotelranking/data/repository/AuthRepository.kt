package com.c22ho01.hotelranking.data.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.data.remote.response.auth.RegisterResponse
import com.c22ho01.hotelranking.data.remote.retrofit.AuthService
import com.c22ho01.hotelranking.utils.wrapEspressoIdlingResource

class AuthRepository(
    private val authService: AuthService
) {

    fun submitRegister(
        userName: String,
        email: String,
        password: String,
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = authService.register(userName, email, password)
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: RegisterResponse()))
                } else {
                    emit(Result.Error(response.body()?.message ?: "Error"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun submitLogin(
        userName: String,
        password: String,
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = authService.login(userName, password)
                if (response.isSuccessful) {
                    emit(Result.Success(response.body() ?: LoginResponse()))
                } else {
                    emit(Result.Error(response.body()?.message ?: "Error"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    companion object {

        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(authService: AuthService): AuthRepository =
            instance
                ?: synchronized(this) {
                    instance
                        ?: AuthRepository(
                            authService,
                        )
                            .also { instance = it }
                }
    }
}