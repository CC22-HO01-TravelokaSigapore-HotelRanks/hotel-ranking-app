package com.c22ho01.hotelranking.data.remote.response.profile

import com.google.gson.annotations.SerializedName

data class ProfileGetResponse(
    @field:SerializedName("data")
    val profileData: ProfileData? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class ProfileData(

    @field:SerializedName("search_history")
    val searchHistory: List<String?>? = null,

    @field:SerializedName("stay_history")
    val stayHistory: List<String?>? = null,

    @field:SerializedName("special_needs")
    val specialNeeds: List<String?>? = null,

    @field:SerializedName("birth_date")
    val birthDate: String? = null,

    @field:SerializedName("nid")
    val nid: Int? = null,

    @field:SerializedName("userName")
    val userName: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("family")
    val family: Boolean? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("hobby")
    val hobby: List<String?>? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)
