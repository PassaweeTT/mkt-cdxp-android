package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services

import android.os.Build

class MktUtils {
    companion object {
        fun endLine(): String {
            var stringList = mutableListOf<String>()
            for (i in 1..10) {
                stringList.add("🛑")
            }
            return stringList.joinToString("")
        }

        fun getDeviceInfo(): String {
            val deviceName: String = Build.MODEL.toString()
            val deviceBrand: String = Build.BRAND.toString()
            return "${deviceBrand}_${deviceName}"
        }

        fun getOsInfo(): String {
            val osName: String = "Android"
            val osVersion: String = Build.VERSION.RELEASE.toString()
            return "${osName}_${osVersion}"
        }
    }
}