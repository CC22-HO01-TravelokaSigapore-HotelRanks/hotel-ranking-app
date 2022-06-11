package com.c22ho01.hotelranking.data.remote.response.review

import com.google.gson.annotations.SerializedName

data class PostReviewResponse(
    @field:SerializedName("message")
    val message: String? = null,
)