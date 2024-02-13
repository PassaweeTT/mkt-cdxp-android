package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services

class Enum {
    enum class MktEvent(val value: String) {
        pageVisit("page_visit"),
        sessionStart("session_start"),
    }

    enum class MktEventField(val value: String) {
        referrer("referrer"),
    }
}
