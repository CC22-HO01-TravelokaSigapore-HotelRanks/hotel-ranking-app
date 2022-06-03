package com.c22ho01.hotelranking.utils

import com.c22ho01.hotelranking.data.remote.response.error.ErrorResponse
import com.c22ho01.hotelranking.data.remote.response.error.SingleErrorResponse
import com.google.gson.Gson
import retrofit2.Response

object ErrorUtils {

    fun <T : Any?> showErrorFromResponse(errorResponse: Response<T>): String {
        val error: ErrorResponse = try {
            Gson().fromJson(
                errorResponse.errorBody()?.string(),
                SingleErrorResponse::class.java
            )
        } catch (e: Exception) {
            SingleErrorResponse(
                errorResponse.errorBody()?.string(),
                errorResponse.message()
            )
        }
        return error.getError() ?: "Unknown error"
    }
}