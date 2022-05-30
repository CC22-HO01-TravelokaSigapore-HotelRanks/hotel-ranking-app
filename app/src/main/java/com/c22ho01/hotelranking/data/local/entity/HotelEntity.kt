package com.c22ho01.hotelranking.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "hotel")
data class HotelEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val typeNearbyDestination: List<String>,
    val image: List<String>,
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val neighborhood: String? = null,
    val star: Double? = null,
    val pricePerNight: Int? = null,
    val freeRefund: Boolean? = false,
    val nearbyDestination: Int? = null,
    val breakfast: Boolean? = false,
    val pool: Boolean? = false,
    val wifi: Boolean? = false,
    val parking: Boolean? = false,
    val smoking: Boolean? = false,
    val airConditioner: Boolean? = false,
    val wheelChairAccess: Boolean? = false,
    val averageBedSize: Boolean? = false,
    val staffVaccinated: Boolean? = false,
    val childArea: Boolean? = false,
    val priceCategory: Int? = null
) : Parcelable