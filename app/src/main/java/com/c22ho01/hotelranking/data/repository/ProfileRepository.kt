package com.c22ho01.hotelranking.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.local.entity.DisabilityEntity
import com.c22ho01.hotelranking.data.local.entity.HobbyEntity
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.data.remote.response.profile.ProfileGetResponse
import com.c22ho01.hotelranking.data.remote.retrofit.ProfileService
import com.c22ho01.hotelranking.utils.DateUtils
import com.c22ho01.hotelranking.utils.ErrorUtils
import com.c22ho01.hotelranking.utils.wrapEspressoIdlingResource
import java.util.*

class ProfileRepository(
    private val profileService: ProfileService
) {

    private var _currentProfile = MutableLiveData(ProfileEntity())
    val currentProfile: LiveData<ProfileEntity>
        get() = _currentProfile

    val hobbyList: List<HobbyEntity> = arrayListOf(
        HobbyEntity(1, R.string.selfie, "selfie", R.drawable.ic_baseline_camera_alt_24),
        HobbyEntity(2, R.string.hiking, "hiking", R.drawable.ic_baseline_landscape_24),
        HobbyEntity(3, R.string.eating, "eating", R.drawable.ic_baseline_local_dining_24),
        HobbyEntity(4, R.string.traveling, "traveling", R.drawable.ic_baseline_card_travel_24),
        HobbyEntity(5, R.string.shopping, "shopping", R.drawable.ic_baseline_shopping_cart_24),
        HobbyEntity(6, R.string.swimming, "swimming", R.drawable.ic_pool),
    )
    val disabilityList: List<DisabilityEntity> = arrayListOf(
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


    fun getProfile(userToken: String): LiveData<Result<ProfileEntity>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading)
            try {
                val response = profileService.getUserById(
                    userToken,
                    currentProfile.value?.id ?: -1,
                )
                if (response.isSuccessful) {
                    var profile = ProfileEntity.fromGetResponse(
                        response.body() ?: ProfileGetResponse(),
                    )
                    val responseData = response.body()?.profileData
                    val hobbies = responseData?.hobby?.map { hobbyResponse ->
                        hobbyList.find { hobbyRepo ->
                            hobbyRepo.fromResponseLabel == hobbyResponse?.let { raw ->
                                Regex("[^A-Za-z0-9 ]").replace(raw, "")
                            }
                        }
                    }

                    val disabilities = responseData?.specialNeeds?.map { specialNeedsResponse ->
                        disabilityList.find { disabilityRepo ->
                            disabilityRepo.fromResponseLabel == specialNeedsResponse?.let { raw ->
                                Regex("[^A-Za-z0-9 ]").replace(raw, "")
                            }
                        }
                    }

                    profile = profile.copy(
                        hobby = hobbies?.toList(),
                        specialNeeds = disabilities?.toList()
                    )
                    _currentProfile.postValue(profile)
                    emit(Result.Success(profile))
                } else {
                    val error = ErrorUtils.showErrorFromResponse(response)
                    emit(Result.Error(error))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun updateProfile(userToken: String, profile: ProfileEntity): LiveData<Result<ProfileEntity>> =
        liveData {
            wrapEspressoIdlingResource {
                emit(Result.Loading)
                try {
                    val response = profileService.updateUserById(
                        token = userToken,
                        id = profile.id,
                        name = profile.name,
                        birthDate = DateUtils.formatDateToStringDash(
                            profile.birthDate
                                ?: Date()
                        ),
                        nid = profile.nid,
                        family = profile.family,
                        hobby = profile.hobby?.map { it?.fromResponseLabel }?.toList()
                            ?.joinToString(),
                        specialNeeds = profile.specialNeeds?.map { it?.fromResponseLabel }?.toList()
                            ?.joinToString(),
                        searchHistory = profile.searchHistory?.joinToString(","),
                        stayHistory = profile.stayHistory?.joinToString(","),
                    )

                    if (response.isSuccessful) {
                        _currentProfile.postValue(profile)
                        emit(Result.Success(profile))
                    } else {
                        val error = ErrorUtils.showErrorFromResponse(response)
                        emit(Result.Error(error))
                    }
                } catch (e: Exception) {
                    emit(Result.Error(e.message.toString()))
                }
            }
        }

    fun setProfileId(profileId: Int) {
        _currentProfile.postValue(
            _currentProfile.value?.copy(id = profileId) ?: ProfileEntity(id = profileId)
        )
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