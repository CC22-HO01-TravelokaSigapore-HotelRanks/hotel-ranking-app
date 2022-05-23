package com.c22ho01.hotelranking.data.remote.response.hotel

import com.google.gson.annotations.SerializedName

data class HotelData(
    @SerializedName("type_nearby_destination")
    val typeNearbyDestination: List<String>,

    @SerializedName("image_links")
    val image: List<String>,

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("neighborhood")
    val neighborhood: String,

    @SerializedName("hotel_star")
    val star: Double,

    @SerializedName("price_per_night")
    val pricePerNight: Int,

    @SerializedName("free_refund")
    val freeRefund: Boolean,

    @SerializedName("nearby_destination")
    val nearbyDestination: Int,

    @SerializedName("breakfast")
    val breakfast: Boolean,

    @SerializedName("pool")
    val pool: Boolean,

    @SerializedName("wifi")
    val wifi: Boolean,

    @SerializedName("parking")
    val parking: Boolean,

    @SerializedName("smoking")
    val smoking: Boolean,

    @SerializedName("air_conditioner")
    val airConditioner: Boolean,

    @SerializedName("wheelchair_access")
    val wheelChairAccess: Boolean,

    @SerializedName("average_bed_size")
    val averageBedSize: Boolean,

    @SerializedName("staff_vaccinated")
    val staffVaccinated: Boolean,

    @SerializedName("child_area")
    val childArea: Boolean,

    @SerializedName("price_category")
    val priceCategory: Int
)
