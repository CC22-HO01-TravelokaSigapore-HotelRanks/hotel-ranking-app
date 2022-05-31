package com.c22ho01.hotelranking.data.remote.response.profile

import com.google.gson.annotations.SerializedName

data class ProfilePutResponse(

    @field:SerializedName("data")
    val data: List<Int?>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)
