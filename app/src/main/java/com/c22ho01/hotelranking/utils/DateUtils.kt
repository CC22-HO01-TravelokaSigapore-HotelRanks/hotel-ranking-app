package com.c22ho01.hotelranking.utils

import android.annotation.SuppressLint
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun parseDateFromString(date: String): Date? {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
    }

    fun formatDateToStringSlash(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }

    fun formatDateToStringDash(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

    fun parseISODateFromString(date: String): Date? {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(date)
    }

    fun formatDateToTimeAgo(date: String): String {
        val sdf =
            parseISODateFromString(date)?.time
        val now = System.currentTimeMillis()
        return if (sdf != null) DateUtils.getRelativeTimeSpanString(
            sdf,
            now,
            DateUtils.MINUTE_IN_MILLIS
        ).toString() else ""
    }
}