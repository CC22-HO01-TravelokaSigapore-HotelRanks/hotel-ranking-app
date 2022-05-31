package com.c22ho01.hotelranking.data.local.entity

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class HobbyEntity(
    val id: Int,
    @StringRes val localizedLabel: Int,
    val fromResponseLabel: String,
    @DrawableRes val icon: Int
) : Parcelable