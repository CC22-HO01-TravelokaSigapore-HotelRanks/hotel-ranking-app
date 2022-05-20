package com.c22ho01.hotelranking.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory
private constructor(
//    private val authRepository: AuthRepository,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
//            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
//                LoginViewModel(authRepository) as T
//            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context) =
                instance
                        ?: synchronized(this) {
                            instance
                                    ?: ViewModelFactory(
//                            RepositoryInjection.provideAuthRepository(),
                                    )
                                            .also { instance = it }
                        }
    }
}