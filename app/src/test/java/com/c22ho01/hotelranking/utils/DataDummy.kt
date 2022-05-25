package com.c22ho01.hotelranking.utils

import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.local.entity.DisabilityEntity
import com.c22ho01.hotelranking.data.local.entity.HobbyEntity
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.data.remote.response.profile.ProfileData
import com.c22ho01.hotelranking.data.remote.response.profile.ProfileGetResponse

object DataDummy {
    private const val dummyDateStr = "2022-05-25"
    private const val dummyISODateStr = "2022-05-18T11:44:15.000Z"
    private val dummyHobbies = arrayListOf("selfie", "hiking")
    private val dummyDisabilities = arrayListOf("tunawicara", "tunarungu")

    fun provideHobbyList(): List<HobbyEntity> = arrayListOf(
        HobbyEntity(1, R.string.selfie, "selfie", R.drawable.ic_baseline_camera_alt_24),
        HobbyEntity(2, R.string.hiking, "hiking", R.drawable.ic_baseline_landscape_24),
        HobbyEntity(3, R.string.eating, "eating", R.drawable.ic_baseline_local_dining_24),
        HobbyEntity(4, R.string.traveling, "traveling", R.drawable.ic_baseline_card_travel_24),
        HobbyEntity(5, R.string.shopping, "shopping", R.drawable.ic_baseline_shopping_cart_24),
        HobbyEntity(6, R.string.swimming, "swimming", R.drawable.ic_baseline_pool_24),
    )

    fun provideDisabilityList(): List<DisabilityEntity> = arrayListOf(
        DisabilityEntity(
            1,
            R.string.i_cant_move_freely,
            "tunadaksa",
            R.drawable.ic_baseline_accessible_24
        ),
        DisabilityEntity(
            2,
            R.string.i_cant_speak_easily,
            "tunawicara",
            R.drawable.ic_baseline_volume_mute_24
        ),
        DisabilityEntity(
            3,
            R.string.i_cant_hear_properly,
            "tunarungu",
            R.drawable.ic_baseline_hearing_24
        ),
        DisabilityEntity(
            4,
            R.string.i_cant_see_clearly,
            "tunanetra",
            R.drawable.ic_baseline_visibility_24
        ),
    )

    fun provideProfileResponse(): ProfileGetResponse {
        return ProfileGetResponse(
            profileData = ProfileData(
                id = 1,
                name = "name",
                email = "email",
                userName = "userName",
                birthDate = dummyDateStr,
                nid = 11111,
                createdAt = dummyISODateStr,
                updatedAt = dummyISODateStr,
                family = true,
                specialNeeds = dummyDisabilities,
                hobby = dummyHobbies,
            )
        )
    }

    fun provideProfileEntity(): ProfileEntity {
        return ProfileEntity(
            id = 1,
            name = "name",
            email = "email",
            userName = "userName",
            birthDate = DateUtils.parseDateFromString(dummyDateStr),
            nid = 11111,
            createdAt = DateUtils.parseISODateFromString(dummyISODateStr),
            updatedAt = DateUtils.parseISODateFromString(dummyISODateStr),
            family = true,
            specialNeeds = arrayListOf(
                DisabilityEntity(
                    2,
                    R.string.i_cant_speak_easily,
                    dummyDisabilities[0],
                    R.drawable.ic_baseline_volume_mute_24
                ),
                DisabilityEntity(
                    3,
                    R.string.i_cant_hear_properly,
                    dummyDisabilities[1],
                    R.drawable.ic_baseline_hearing_24
                )
            ),
            hobby = arrayListOf(
                HobbyEntity(
                    1,
                    R.string.selfie,
                    dummyHobbies[0],
                    R.drawable.ic_baseline_camera_alt_24
                ),
                HobbyEntity(
                    2,
                    R.string.hiking,
                    dummyHobbies[1],
                    R.drawable.ic_baseline_landscape_24
                ),
            ),
        )
    }
}
