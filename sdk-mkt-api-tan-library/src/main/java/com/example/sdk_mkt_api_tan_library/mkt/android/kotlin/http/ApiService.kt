package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.http

import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.ConsentParamModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.ConsentResponseModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.IdentifyModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.InitialModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.RevokeConsentParamModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.SessionModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.SetConsentParamModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.SetConsentResponseModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.SystemTimeModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.TrackParamModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.UpdateConsentParamModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.UpdateCustResModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.UpdateCustomerModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.UpdateSessionModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @GET("system-time")
    fun getSystemTime(): Call<SystemTimeModel>


    @POST("initialize")
    fun initialize(
        @Header("secret-key") appKey: String,
        @Body param: InitialModel,
    ): Call<InitialModel>

    @POST("customer/events")
    fun track(
        @Header("secret-key") appKey: String,
        @Body param: TrackParamModel,
    ): Call<Unit>

    @POST("update-session")
    fun updateSession(
        @Header("secret-key") appKey: String,
        @Body param: UpdateSessionModel,
    ): Call<Unit>

    @POST("anonymous")
    fun anonymous(
        @Header("secret-key") appKey: String,
        @Body param: SessionModel,
    ): Call<Unit>

    @POST("customer")
    fun updateCustomer(
        @Header("secret-key") appKey: String,
        @Body param: UpdateCustomerModel,
    ): Call<UpdateCustResModel>

    @POST("customer/identify")
    fun identifyCustomer(
        @Header("secret-key") appKey: String,
        @Body param: IdentifyModel,
    ): Call<Unit>

    @POST("customer/consents")
    fun consent(
        @Header("secret-key") appKey: String,
        @Body param: ConsentParamModel,
    ): Call<ConsentResponseModel>

    @POST("add-consent")
    fun setConsent(
        @Header("secret-key") appKey: String,
        @Body param: SetConsentParamModel,
    ): Call<SetConsentResponseModel>

    @POST("update-consent")
    fun updateConsent(
        @Header("secret-key") appKey: String,
        @Body param: UpdateConsentParamModel,
    ): Call<SetConsentResponseModel>

    @POST("delete-consent")
    fun revokeConsent(
        @Header("secret-key") appKey: String,
        @Body param: RevokeConsentParamModel,
    ): Call<Unit>
}