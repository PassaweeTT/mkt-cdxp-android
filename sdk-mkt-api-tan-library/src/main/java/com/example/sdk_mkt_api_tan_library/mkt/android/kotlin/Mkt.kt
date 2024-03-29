package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.http.ApiService
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.ConsentParamModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.ConsentResponseModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.SessionModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.SystemTimeModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.TrackParamModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.UpdateCustResModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.UpdateCustomerModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.UpdateSessionModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.IdentifyModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.models.InitialModel
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.helper.DateHelper
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.helper.JsonHelper
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.Log.mLog
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.Log.mTag
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.MktUtils
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.helper.PrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class Mkt {
    private var application: Application? = null

    private var projId: Long? = null
    private var appKey: String? = null
    private var auto: Boolean = true
    private var visit: Boolean = true
    private var defaultProperties: Map<String, Any>? = null
    private var referrer: String = ""
    private var urlEndPoint: String = "https://api.mkt.com/"

    private var isSetTimer: Boolean = false

    companion object {
        private var application: Application? = null
        private var sysMkt = Mkt()

        fun clearPref() {
            PrefHelper.clearPref()
        }

        fun getPref(): Map<String, *>? {
            return PrefHelper.getAllPref()
        }

        fun appReady(application: Application, referrer: String?) {
            sysMkt.application = application
            PrefHelper.setApplication(application)

            val fileName = "cnx-mkt-config.json"
            val jsonObject = JsonHelper.readJsonFromAssets(application, fileName)

            if (!referrer.isNullOrBlank()) {
                sysMkt.referrer = referrer
            }

            if (jsonObject != null) {
                val projectConfig = jsonObject.getJSONObject("project_config")
                sysMkt.urlEndPoint = "${projectConfig.getString("url_end_point")}api/Track/"
                sysMkt.appKey = projectConfig.getString("app_key")
                sysMkt.projId = projectConfig.getLong("project_id")
                sysMkt.auto = projectConfig.getJSONObject("track").getBoolean("auto")
                sysMkt.visit = projectConfig.getJSONObject("track").getBoolean("visit")
                val defaultPropertiesJson =
                    projectConfig.getJSONObject("track").getJSONObject("default_properties")

                val defaultPropertiesMap = JsonHelper.jsonObjectToMap(defaultPropertiesJson)
                val generalMap = mutableMapOf<String, Any>()
                generalMap["device"] = MktUtils.getDeviceInfo()
                generalMap["os"] = MktUtils.getOsInfo()
                generalMap["browser"] = ""
                generalMap["www_location"] = ""

                generalMap.putAll(defaultPropertiesMap)

                sysMkt.defaultProperties = generalMap
            } else {
                Log.d(mTag.Mkt, "config file not found")
                throw Exception("config file not found")
            }


        }

        fun initialize(callback: MktCallBack<String>?, location: String?): Unit {
            var param = InitialModel(
                projId = sysMkt.projId!!,
                hardId = "",
                referrer = sysMkt.referrer,
                cookie = PrefHelper.getPref("cookie") ?: "",
                eventTime = Date(),
                data = sysMkt.getGeneralEvent(location ?: ""),
                session = SessionModel(
                    sessionId = PrefHelper.getPref("sessionId"),
                    sessionStart = PrefHelper.getPref("sessionStart")
                        ?.let { DateHelper.convertStringToDate(it) },
                    sessionLast = PrefHelper.getPref("sessionLast")
                        ?.let { DateHelper.convertStringToDate(it) },
                ),
                auto = sysMkt.auto,
                visit = sysMkt.visit,
            )
            sysMkt.initializeApp(param, callback, location)
        }

        fun systemTime(callback: MktCallBack<Date?>): Unit {
            sysMkt.getSystemTime(callback)
        }

        fun track(
            eventName: String,
            eventData: Map<String, Any>?,
            callback: MktCallBack<String?>?,
            location: String?,
        ): Unit {
            if (eventName.isBlank()) {
                callback?.onFailed("Event name is required")
                return
            }
            if (eventData.isNullOrEmpty()) {
                callback?.onFailed("Event data is required")
                return
            }

            var param: TrackParamModel = TrackParamModel(
                cookie = PrefHelper.getPref("cookie") ?: "",
                data = sysMkt.getGeneralEvent(location ?: ""),
                eventName = eventName,
                eventData = eventData
            )
            sysMkt.trackEvent(param, callback)
        }

        fun identify(
            hardId: String?,
            customerData: Map<String, Any>?,
            callback: MktCallBack<String?>?,
            location: String?,
        ): Unit {
            val param: IdentifyModel = IdentifyModel(
                hardId = hardId ?: "",
                cookie = PrefHelper.getPref("cookie") ?: "",
                data = sysMkt.getGeneralEvent(location ?: ""),
                customerData = customerData
            )
            sysMkt.identifyCustomer(param, callback)
        }

        fun anonymous(callback: MktCallBack<String?>?): Unit {
            val sessionParam = SessionModel(
                sessionId = PrefHelper.getPref("sessionId"),
                sessionStart = PrefHelper.getPref("sessionStart")
                    ?.let { DateHelper.convertStringToDate(it) },
                sessionLast = PrefHelper.getPref("sessionLast")
                    ?.let { DateHelper.convertStringToDate(it) },
            )
            sysMkt.setAnonymous(sessionParam, callback)
        }

        fun update(
            hardId: String?,
            customerData: Map<String, Any>?,
            callback: MktCallBack<UpdateCustResModel>?,
        ): Unit {
            val param: UpdateCustomerModel = UpdateCustomerModel(
                hardId = hardId ?: "",
                cookie = PrefHelper.getPref("cookie") ?: "",
                customerData = customerData
            )
            sysMkt.updateCustomer(param, callback)
        }

        fun consent(callback: MktCallBack<List<Map<String, Any>>>?): Unit {
            val param = ConsentParamModel(cookie = PrefHelper.getPref("cookie") ?: "")
            sysMkt.getConsent(param, callback)
        }
    }

    private fun getSystemTime(callback: MktCallBack<Date?>): Unit {
        val retrofit = Retrofit.Builder()
            .baseUrl(sysMkt.urlEndPoint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val call = retrofit.getSystemTime()
        call.enqueue(object : Callback<SystemTimeModel> {
            override fun onResponse(
                call: Call<SystemTimeModel>,
                response: Response<SystemTimeModel>,
            ) {
                if (response.isSuccessful) {
                    val systemTimeModel = response.body()
                    mLog.systemTime(true, null)
                    callback.onSuccess(systemTimeModel?.dateTime)
                } else {
                    mLog.systemTime(false, response.code().toString())
                    callback.onFailed("system time failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SystemTimeModel>, t: Throwable) {
                mLog.systemTime(false, t.message)
                callback.onFailed("system time failed : ${t.message}")
            }
        })

    }

    private fun initializeApp(
        param: InitialModel,
        callback: MktCallBack<String>?,
        location: String?,
    ): Unit {
        val urlPath = getUrlProj()

        val retrofit = Retrofit.Builder()
            .baseUrl(urlPath)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val call = retrofit.initialize(sysMkt.appKey!!, param)
        call.enqueue(object : Callback<InitialModel> {
            override fun onResponse(
                call: Call<InitialModel>,
                response: Response<InitialModel>,
            ) {
                if (response.isSuccessful) {
                    val initRes = response.body()!!
                    PrefHelper.setPref("cookie", initRes.cookie ?: "")
                    PrefHelper.setPref("sessionId", initRes.session.sessionId ?: "")
                    PrefHelper.setPref(
                        "sessionStart",
                        initRes.session.sessionStart?.let { DateHelper.convertDateToString(it) }
                            ?: "")
                    PrefHelper.setPref(
                        "sessionLast",
                        initRes.session.sessionLast?.let { DateHelper.convertDateToString(it) }
                            ?: "")

                    if (sysMkt.auto) {
                        setTimerUpdateSession(60000)
                    }

                    mLog.init(true, null)
                    callback?.onSuccess("init success")
                } else {
                    callback?.onFailed("init : ${response.code()}")
                    mLog.init(false, response.code().toString())
                }
            }

            override fun onFailure(call: Call<InitialModel>, t: Throwable) {
                callback?.onFailed("init failed : ${t.message}")
                mLog.init(false, t.message)
            }
        })

    }

    private fun trackEvent(eventParam: TrackParamModel, callback: MktCallBack<String?>?): Unit {
        val urlPath = getUrlProj()

        val retrofit = Retrofit.Builder()
            .baseUrl(urlPath)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val call = retrofit.track(sysMkt.appKey!!, eventParam)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>,
            ) {
                if (response.isSuccessful) {
                    mLog.track(true, null)
                    callback?.onSuccess("track success ${response.body()} ${eventParam.eventName}")
                } else {
                    mLog.track(false, response.code().toString())
                    callback?.onFailed("track failed ${eventParam.eventName}: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                mLog.track(false, t.message)
                callback?.onFailed("track failed ${eventParam.eventName}: ${t.message}")
            }
        })
    }

    private fun setTimerUpdateSession(delayTime: Long) {
        val timer = Timer()

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateSessionStart()
            }
        }, delayTime, 60000)
    }

    private fun updateSessionStart() {
        val sessionParam = SessionModel(
            sessionId = PrefHelper.getPref("sessionId"),
            sessionStart = PrefHelper.getPref("sessionStart")
                ?.let { DateHelper.convertStringToDate(it) },
            sessionLast = PrefHelper.getPref("sessionLast")
                ?.let { DateHelper.convertStringToDate(it) },
        )

        val param = UpdateSessionModel(sessionParam)

        val urlPath = getUrlProj()

        val retrofit = Retrofit.Builder()
            .baseUrl(urlPath)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val call = retrofit.updateSession(sysMkt.appKey!!, param)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>,
            ) {
                if (response.isSuccessful) {
                    Log.d(mTag.Track, "updateSession success : ${Date()}")
                    PrefHelper.setPref("sessionLast", DateHelper.convertDateToString(Date()))

                } else {
                    Log.d(mTag.Track, "updateSession failed : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d(mTag.Track, "updateSession failed : ${t.message}")
            }
        })
    }

    private fun identifyCustomer(param: IdentifyModel, callback: MktCallBack<String?>?) {
        val urlPath = getUrlProj()

        val retrofit = Retrofit.Builder()
            .baseUrl(urlPath)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val call = retrofit.identifyCustomer(sysMkt.appKey!!, param)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>,
            ) {
                if (response.isSuccessful) {
                    Log.d(mTag.Track, "identifyCustomer success")
                    callback?.onSuccess("identify success")
                } else {
                    Log.d(mTag.Track, "identifyCustomer failed : ${response.code()}")
                    callback?.onFailed("identify failed : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d(mTag.Track, "identifyCustomer failed : ${t.message}")
                callback?.onFailed("identify failed : ${t.message}")
            }
        })
    }

    private fun setAnonymous(param: SessionModel, callback: MktCallBack<String?>?) {
        val urlPath = getUrlProj()

        val retrofit = Retrofit.Builder()
            .baseUrl(urlPath)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val call = retrofit.anonymous(sysMkt.appKey!!, param)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>,
            ) {
                if (response.isSuccessful) {
                    PrefHelper.clearPref()
                    initialize(null, sysMkt.getRunningActivity())
                    Log.d(mTag.Track, "anonymius success")
                    callback?.onSuccess("anonymius success")

                } else {
                    Log.d(mTag.Track, "anonymius failed : ${response.code()}")
                    callback?.onFailed("anonymius failed : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d(mTag.Track, "anonymius failed : ${t.message}")
                callback?.onFailed("anonymius failed : ${t.message}")
            }
        })
    }

    private fun updateCustomer(
        param: UpdateCustomerModel,
        callback: MktCallBack<UpdateCustResModel>?,
    ) {
        val urlPath = getUrlProj()

        val retrofit = Retrofit.Builder()
            .baseUrl(urlPath)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val call = retrofit.updateCustomer(sysMkt.appKey!!, param)
        call.enqueue(object : Callback<UpdateCustResModel> {
            override fun onResponse(
                call: Call<UpdateCustResModel>,
                response: Response<UpdateCustResModel>,
            ) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d(mTag.Track, "updateCustomer success")
                    callback?.onSuccess(res)
                } else {
                    Log.d(mTag.Track, "updateCustomer failed : ${response.code()}")
                    callback?.onFailed("update failed : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UpdateCustResModel>, t: Throwable) {
                Log.d(mTag.Track, "updateCustomer failed : ${t.message}")
                callback?.onFailed("update failed : ${t.message}")
            }
        })
    }

    private fun getConsent(
        param: ConsentParamModel,
        callback: MktCallBack<List<Map<String, Any>>>?,
    ) {
        val urlPath = getUrlProj()

        val retrofit = Retrofit.Builder()
            .baseUrl(urlPath)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val call = retrofit.consent(sysMkt.appKey!!, param)
        call.enqueue(object : Callback<ConsentResponseModel> {
            override fun onResponse(
                call: Call<ConsentResponseModel>,
                response: Response<ConsentResponseModel>,
            ) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d(mTag.Track, "consent success")
                    callback?.onSuccess(res.customerConsent)
                } else {
                    Log.d(mTag.Track, "consent failed : ${response.code()}")
                    callback?.onFailed("consent failed : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ConsentResponseModel>, t: Throwable) {
                Log.d(mTag.Track, "consent failed : ${t.message}")
                callback?.onFailed("consent failed : ${t.message}")
            }
        })
    }

    private fun getUrlProj(): String {
        return "${sysMkt.urlEndPoint}${sysMkt.projId}/"
    }

    private fun getGeneralEvent(location: String): Map<String, Any> {
        val generalMap = mutableMapOf<String, Any>()
        generalMap["www_location"] = location
        generalMap.putAll(sysMkt.defaultProperties!!)
        return generalMap.toMap()
    }

    fun getRunningActivity(): String? {
        val activityManager =
            sysMkt.application!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val runningTaskInfoList = activityManager.getRunningTasks(1)

        return if (runningTaskInfoList.isNotEmpty()) {
            val runningTaskInfo = runningTaskInfoList[0]
            runningTaskInfo.topActivity?.className
        } else {
            null
        }
    }

}