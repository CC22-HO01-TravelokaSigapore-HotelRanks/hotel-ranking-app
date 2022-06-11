package com.c22ho01.hotelranking.data.local.entity

import android.os.Parcelable
import com.c22ho01.hotelranking.data.remote.response.profile.ProfileGetResponse
import com.c22ho01.hotelranking.utils.DateUtils
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*

@Parcelize
data class ProfileEntity(
    val id: Int? = null,
    val userName: String? = null,
    val nid: Int? = null,
    val email: String? = null,
    val name: String? = null,
    val birthDate: Date? = null,
    val family: Boolean? = null,
    val hobby: @RawValue List<HobbyEntity?>? = null,
    val searchHistory: List<String?>? = null,
    val stayHistory: List<String?>? = null,
    val specialNeeds: @RawValue List<DisabilityEntity?>? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val reviewCounter: Int? = null,
) : Parcelable {
    companion object {
        fun fromGetResponse(response: ProfileGetResponse): ProfileEntity {
            val data = response.profileData

            return ProfileEntity(
                id = data?.id,
                userName = data?.userName,
                nid = data?.nid,
                email = data?.email,
                name = data?.name,
                birthDate = data?.birthDate?.let {
                    DateUtils.parseDateFromString(it)
                },
                family = data?.family,
                searchHistory = data?.searchHistory,
                stayHistory = data?.stayHistory,
                createdAt = data?.createdAt?.let {
                    DateUtils.parseISODateFromString(it)
                },
                reviewCounter = data?.reviewCounter,
                updatedAt = data?.updatedAt?.let {
                    DateUtils.parseISODateFromString(it)
                },
            )
        }
    }
}