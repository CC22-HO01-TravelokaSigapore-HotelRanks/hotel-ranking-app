package com.c22ho01.hotelranking.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "hotel")
data class HotelEntity(
        @PrimaryKey val id: String,
        // wip
) : Parcelable