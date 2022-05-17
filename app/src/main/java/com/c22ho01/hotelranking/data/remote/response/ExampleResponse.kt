package com.c22ho01.hotelranking.data.remote.response

import com.google.gson.annotations.SerializedName

data class ExampleResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)