package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin

interface MktCallBack<T> {
    fun onSuccess(result : T)
    fun onFailed(messageError : String)
}