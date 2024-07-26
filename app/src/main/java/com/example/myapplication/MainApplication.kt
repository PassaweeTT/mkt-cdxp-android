package com.example.myapplication

import android.app.Application
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.Mkt

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Mkt.appReady(application = this, referrer = "referrer.com")
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}