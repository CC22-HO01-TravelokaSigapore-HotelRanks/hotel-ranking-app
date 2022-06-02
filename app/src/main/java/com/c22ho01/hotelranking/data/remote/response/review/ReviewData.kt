package com.c22ho01.hotelranking.data.remote.response.review

import com.google.gson.annotations.SerializedName

data class ReviewData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("text")
    val text: String,

    @SerializedName("labels")
    val labels: Int,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("dates")
    val dates: String,

    @SerializedName("user_id")
    val user_id: Int,
    
    @SerializedName("name")
    val name: String,
)
