package com.c22ho01.hotelranking.data.remote.response.error

import com.google.gson.annotations.SerializedName

data class SingleErrorResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("text")
	val text: String? = null
) : ErrorResponse {
	override fun getError() = message ?: text
}
