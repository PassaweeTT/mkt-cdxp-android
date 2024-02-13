package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.helper

import android.app.Application
import android.content.Context

class PrefHelper {
    private var application: Application? = null

    companion object {
        private val sysPref = PrefHelper()
        private val prefName = "MKT-PREF"

        fun  setApplication(application: Application) {
            sysPref.application = application
        }

        fun setPref(key: String, value: String) {
            val sharedPreferences =
                sysPref.application!!.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getPref(key: String): String? {
            val sharedPreferences =
                sysPref.application!!.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, null)
        }

        fun getAllPref(): Map<String, *>? {
            val sharedPreferences =
                sysPref.application!!.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return sharedPreferences.all
        }

        fun  clearPref() {
            val sharedPreferences =
                sysPref.application!!.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
        }
    }
}