package com.c22ho01.hotelranking.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c22ho01.hotelranking.data.repository.*
import com.c22ho01.hotelranking.injection.RepositoryInjection
import com.c22ho01.hotelranking.viewmodel.auth.LoginViewModel
import com.c22ho01.hotelranking.viewmodel.auth.RegisterViewModel
import com.c22ho01.hotelranking.viewmodel.hotel.DetailViewModel
import com.c22ho01.hotelranking.viewmodel.hotel.ForYouViewModel
import com.c22ho01.hotelranking.viewmodel.hotel.HomeViewModel
import com.c22ho01.hotelranking.viewmodel.hotel.SearchViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileCustomizeViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.c22ho01.hotelranking.viewmodel.review.ReviewViewModel
import com.c22ho01.hotelranking.viewmodel.utils.TokenViewModel

class ViewModelFactory
private constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
    private val profileRepository: ProfileRepository,
    private val hotelRepository: HotelRepository,
    private val reviewRepository: ReviewRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TokenViewModel::class.java) -> {
                TokenViewModel(tokenRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(hotelRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(hotelRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(profileRepository, tokenRepository, preferenceRepository) as T
            }
            modelClass.isAssignableFrom(ProfileCustomizeViewModel::class.java) -> {
                ProfileCustomizeViewModel(profileRepository) as T
            }
            modelClass.isAssignableFrom(ReviewViewModel::class.java) -> {
                ReviewViewModel(reviewRepository) as T
            }
            modelClass.isAssignableFrom(ForYouViewModel::class.java) -> {
                ForYouViewModel(tokenRepository, hotelRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(hotelRepository) as T
            }
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
                            RepositoryInjection.provideAuthRepository(),
                            RepositoryInjection.provideTokenRepository(context),
                            RepositoryInjection.provideProfileRepository(context),
                            RepositoryInjection.provideHotelRepository(),
                            RepositoryInjection.provideReviewRepository(),
                            RepositoryInjection.providePreferenceRepository(context)
                        )
                            .also { instance = it }
                }
    }
}