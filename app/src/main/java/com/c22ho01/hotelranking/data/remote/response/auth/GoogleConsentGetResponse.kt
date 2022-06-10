package com.c22ho01.hotelranking.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class GoogleConsentGetResponse(

	@field:SerializedName("code")
	val code: String? = null
)
