package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.helper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class DateHelper {

    companion object {
        fun getDateNow(): LocalDateTime {
            return LocalDateTime.now()
        }

        private fun getDateFormat(): DateFormat {
            return SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
        }

        fun convertStringToDate(stringDate: String): Date? {
            val format = "EEE MMM dd HH:mm:ss 'GMT'Z yyyy"
            val sdf = SimpleDateFormat(format, Locale.ENGLISH)
            return try {
                sdf.parse(stringDate)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun convertDateToString(date: Date): String {
            val format = "EEE MMM dd HH:mm:ss 'GMT'Z yyyy"
            val sdf = SimpleDateFormat(format, Locale.ENGLISH)
            return sdf.format(date)
        }
    }
}