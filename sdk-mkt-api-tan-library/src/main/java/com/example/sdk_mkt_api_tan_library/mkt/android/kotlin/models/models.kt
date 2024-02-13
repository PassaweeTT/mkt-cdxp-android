package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models

import java.util.Date

class models {
}

data class JsonPlaceHolderPost(
    val body: String,
    val id: Int,
    val title: String,
    val userId: Int,
)

data class SystemTimeModel(
    val dateTime: Date?,
)


data class SdkParamModel(
    val projId: Long,
    val hardId: String,
    val referrer: String,
    val cookie: String,
    val eventTime: Date?,
    val data: Map<String, Any>?,
    val session: SessionModel,
)

data class InitialModel(
    val projId: Long,
    val hardId: String,
    val referrer: String,
    val cookie: String,
    val eventTime: Date?,
    val data: Map<String, Any>?,
    val session: SessionModel,
    val auto: Boolean,
    val visit : Boolean
)

data class SessionModel(
    val sessionId: String?,
    val sessionStart: Date?,
    val sessionLast: Date?,
)

data class TrackInputModel(
    val eventName: String,
    val eventData: Map<String, Any>?,
)

data class TrackParamModel(
    val cookie: String,
    val data: Map<String, Any>?,
    val eventName: String,
    val eventData: Map<String, Any>?,
)

data class UpdateSessionModel(
    val session: SessionModel,
)

data class CustomerModel(
    val hardId: String,
    val customerData: Map<String, Any>?,
)

data class UpdateCustomerModel(
    val hardId: String,
    val cookie: String,
    val customerData: Map<String, Any>?,
)

data class UpdateCustResModel(
    val customerData: Map<String, Any>,
)

data class IdentifyModel(
    val hardId: String,
    val cookie: String,
    val customerData: Map<String, Any>?,
    val data: Map<String, Any>?,
)

data class ConsentParamModel(
    val cookie: String,
)
data class ConsentResponseModel(
    val customerConsent: List<Map<String, Any>>
)
