package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.Log

class mLog {
    companion object {
        fun init(isTrue: Boolean, message: String?) {
            if (isTrue) {
                android.util.Log.d(
                    mTag.Init,
                    "\uD83D\uDD11\uD83D\uDD11✅✅[init success${message?.let { " : $it" } ?: ""}]")
            } else {
                android.util.Log.d(
                    mTag.Init,
                    "\uD83D\uDD11\uD83D\uDD11❌❌[init failed : ${message}]"
                )
            }
        }

        fun systemTime(isTrue: Boolean, message: String?) {
            if (isTrue) {
                android.util.Log.d(
                    mTag.SystemTime,
                    "\uD83D\uDD57\uD83D\uDD57✅✅[system time success${message?.let { " : $it" } ?: ""}]"
                )
            } else {
                android.util.Log.d(
                    mTag.SystemTime,
                    "\uD83D\uDD57\uD83D\uDD57❌❌[system time failed : ${message}]"
                )
            }
        }

        fun track(isTrue: Boolean, message: String?) {
            if (isTrue) {
                android.util.Log.d(
                    mTag.Track,
                    "\uD83D\uDD57\uD83D\uDD57✅✅[track success${message?.let { " : $it" } ?: ""}]"
                )
            } else {
                android.util.Log.d(
                    mTag.Track,
                    "\uD83D\uDD57\uD83D\uDD57❌❌[track failed : ${message}]"
                )
            }
        }
    }
}