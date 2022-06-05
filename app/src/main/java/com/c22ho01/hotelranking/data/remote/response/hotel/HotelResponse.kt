package com.c22ho01.hotelranking.data.remote.response.hotel

data class HotelResponse(
    val message: String? = null,
    val total: Int? = null,
    val data: List<HotelData>? = null
)
