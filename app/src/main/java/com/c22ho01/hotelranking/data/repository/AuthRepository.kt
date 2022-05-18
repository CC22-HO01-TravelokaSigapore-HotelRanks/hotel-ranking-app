package com.c22ho01.hotelranking.data.repository

import com.c22ho01.hotelranking.data.remote.retrofit.AuthService

class AuthRepository(
    authService: AuthService
) {

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