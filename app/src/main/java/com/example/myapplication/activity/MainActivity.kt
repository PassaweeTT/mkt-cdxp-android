package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.myapplication.R
import com.example.myapplication.activity.Helper.Dialog
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.Mkt
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.MktCallBack
import com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.Log.mLog
import java.util.Date


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Mkt.initialize(null)

        val btnTime: Button = findViewById(R.id.btnSystemTime)
        btnTime.setOnClickListener {
            Mkt.systemTime(systemTimeCallback)
        }

        val btnAddToCart: Button = findViewById(R.id.btnAddToCart)
        btnAddToCart.setOnClickListener {
            Mkt.track(
                eventName = "cart_update",
                eventData = mapOf(
                    "action" to "Add to cart",
                    "item_id" to "nike-shoes-123",
                    "brand" to "Nike",
                    "price" to 10.99
                ),
                callback = null
            )
        }

        val btnUpdateCustomer: Button = findViewById(R.id.btnUpdateCustomer)
        btnUpdateCustomer.setOnClickListener {
            Mkt.update(
                "", mapOf(
                    "score" to "11",
                    "age" to "21"
                ), null
            )
        }

        val btnConsent: Button = findViewById(R.id.btnConsent)
        btnConsent.setOnClickListener {
            Mkt.consent(consentCallback)
        }

        val btnSetConsent: Button = findViewById(R.id.setConsent)
        btnSetConsent.setOnClickListener {
            Mkt.setConsent(
                consentName = "cookie",
                action = "accept",
                actionTime = Date(), null, actionConsentCallback
            )
        }

        val consentId = "66666d0d6d75dfdbbd50de77"
        val btnUpdateConsent: Button = findViewById(R.id.updateConsent)
        btnUpdateConsent.setOnClickListener {
            Mkt.updateConsent(
                consentId = consentId,
                consentName = "cookie",
                action = "reject",
                actionTime = Date(), null, actionConsentCallback
            )
        }

        val btnRevokeConsent: Button = findViewById(R.id.revokeConsent)
        btnRevokeConsent.setOnClickListener {
            Mkt.revokeConsent(
                consentId = consentId,
                callback = revokeConsentCallback,
            )
        }

        val btnLogin: Button = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            Mkt.identify("sawada@vongola.net", null, null)
        }

        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            Mkt.anonymous(null)
        }

        val btnClear: Button = findViewById(R.id.btnClear)
        btnClear.setOnClickListener {
            Mkt.clearPref()
            Dialog().showDialog(this@MainActivity, "Shared Preferences", "Clear Shared Preferences")
        }

        val btnGetPref: Button = findViewById(R.id.btnGetPref)
        btnGetPref.setOnClickListener {
            val allPref = Mkt.getPref()

            if (allPref != null && allPref.isNotEmpty()) {
                val stringBuilder = StringBuilder()

                allPref.forEach { (key, value) ->
                    stringBuilder.append("$key : $value \n\n")
                }
                Dialog().showDialog(
                    this@MainActivity,
                    "Shared Preferences",
                    stringBuilder.toString()
                )
            } else {
                Dialog().showDialog(
                    this@MainActivity,
                    "Shared Preferences",
                    "No preferences found."
                )
            }
        }
    }

    private val systemTimeCallback = object : MktCallBack<Date?> {
        override fun onSuccess(result: Date?) {
            mLog.systemTime(true, "Time : ${result.toString()}")
            Dialog().showDialog(this@MainActivity, "System Time", "Time : ${result.toString()}")
        }

        override fun onFailed(messageError: String) {
            mLog.systemTime(false, messageError)
            Dialog().showDialog(
                this@MainActivity,
                "System Time Failed",
                "Error : $messageError"
            )
        }
    }

    private val consentCallback = object : MktCallBack<List<Map<String, Any>>> {
        override fun onSuccess(result: List<Map<String, Any>>) {
            val consentList = mutableListOf<String>()
            result.forEach { map ->
                map.forEach { (key, value) ->
                    consentList.add("$key : $value")
                }
            }
            Dialog().showDialog(
                this@MainActivity,
                "Consent",
                consentList.joinToString("\n\n")
            )
        }

        override fun onFailed(messageError: String) {
            mLog.systemTime(false, messageError)
            Dialog().showDialog(
                this@MainActivity,
                "Consent Failed",
                "Error : $messageError"
            )
        }
    }

    private val actionConsentCallback = object : MktCallBack<String> {
        override fun onSuccess(result: String) {
            Log.d("consent", result)
        }

        override fun onFailed(messageError: String) {
            Log.d("consent", messageError)
        }
    }

    private val revokeConsentCallback = object : MktCallBack<String?> {
        override fun onSuccess(result: String?) {
            Log.d("consent", result ?: "")
        }

        override fun onFailed(messageError: String) {
            Log.d("consent", messageError)
        }
    }

}