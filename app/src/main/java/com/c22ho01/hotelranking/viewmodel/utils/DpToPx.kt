package com.c22ho01.hotelranking.viewmodel.utils

import android.content.res.Resources

val Int.dpToPx get() = (this * Resources.getSystem().displayMetrics.density).toInt()
