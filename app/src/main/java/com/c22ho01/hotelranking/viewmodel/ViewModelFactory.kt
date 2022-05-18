package com.c22ho01.hotelranking.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c22ho01.hotelranking.data.repository.AuthRepository
import com.c22ho01.hotelranking.injection.RepositoryInjection
import com.c22ho01.hotelranking.viewmodel.auth.LoginViewModel
import com.c22ho01.hotelranking.viewmodel.auth.RegisterViewModel

class ViewModelFactory
private constructor(
    private val authRepository: AuthRepository,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile private var instance: ViewModelFactory? = null

        fun getInstance(context: Context) =
            instance
                ?: synchronized(this) {
                    instance
                        ?: ViewModelFactory(
                            RepositoryInjection.provideAuthRepository(),
                        )
                            .also { instance = it }
                }
    }
}