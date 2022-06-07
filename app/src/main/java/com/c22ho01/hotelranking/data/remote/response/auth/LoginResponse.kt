package com.c22ho01.hotelranking.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("data")
    val loginData: LoginData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class LoginData(

    @field:SerializedName("userId")
    val userId: Int? = null,

    @field:SerializedName("accessToken")
    val accessToken: String? = null,

    @field:SerializedName("refreshToken")
    val refreshToken: String? = null
)
