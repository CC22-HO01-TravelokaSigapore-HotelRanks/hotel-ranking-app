package com.c22ho01.hotelranking.data.repository

import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.local.entity.DisabilityEntity
import com.c22ho01.hotelranking.data.local.entity.HobbyEntity
import com.c22ho01.hotelranking.data.remote.retrofit.ProfileService

class ProfileRepository(
    private val profileService: ProfileService
) {
    private val _hobbyList: List<HobbyEntity> = arrayListOf(
        HobbyEntity(1, R.string.selfie, "selfie", R.drawable.ic_baseline_camera_alt_24),
        HobbyEntity(2, R.string.hiking, "hiking", R.drawable.ic_baseline_landscape_24),
        HobbyEntity(3, R.string.eating, "eating", R.drawable.ic_baseline_local_dining_24),
        HobbyEntity(4, R.string.traveling, "traveling", R.drawable.ic_baseline_card_travel_24),
        HobbyEntity(5, R.string.shopping, "shopping", R.drawable.ic_baseline_shopping_cart_24),
        HobbyEntity(6, R.string.swimming, "swimming", R.drawable.ic_baseline_pool_24),
    )
    private val _disabilityList: List<DisabilityEntity> = arrayListOf(
        DisabilityEntity(1, R.string.i_cant_move_freely, "tunadaksa", R.drawable.ic_baseline_accessible_24),
        DisabilityEntity(2, R.string.i_cant_speak_easily, "tunawicara", R.drawable.ic_baseline_volume_mute_24),
        DisabilityEntity(3, R.string.i_cant_hear_properly, "tunarungu", R.drawable.ic_baseline_hearing_24),
        DisabilityEntity(4, R.string.i_cant_see_clearly, "tunanetra", R.drawable.ic_baseline_visibility_24),
    )

    fun getHobbyList(): List<HobbyEntity> {
        return _hobbyList
    }

    fun getDisabilityList(): List<DisabilityEntity> {
        return _disabilityList
    }

    companion object {

        @Volatile
        private var instance: ProfileRepository? = null

        fun getInstance(profileService: ProfileService): ProfileRepository =
            instance
                ?: synchronized(this) {
                    instance
                        ?: ProfileRepository(
                            profileService,
                        )
                            .also { instance = it }
                }
    }
}