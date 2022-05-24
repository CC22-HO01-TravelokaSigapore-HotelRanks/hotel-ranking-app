package com.c22ho01.hotelranking.data.local.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class DisabilityEntity(
    val id: Int,
    @StringRes val localizedLabel: Int,
    val fromResponseLabel: String,
    @DrawableRes val icon: Int
)