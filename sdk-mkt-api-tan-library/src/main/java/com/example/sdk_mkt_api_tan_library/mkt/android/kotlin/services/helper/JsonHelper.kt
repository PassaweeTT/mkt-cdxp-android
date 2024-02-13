package com.example.sdk_mkt_api_tan_library.mkt.android.kotlin.services.helper

import android.content.Context
import org.json.JSONObject
import java.io.IOException

class JsonHelper {
    companion object {
        fun readJsonFromAssets(context: Context, fileName: String): JSONObject? {
            val json: String?
            try {
                val inputStream = context.assets.open(fileName)
                json = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            return JSONObject(json)
        }

        fun jsonObjectToMap(jsonObject: JSONObject): MutableMap<String, Any> {
            val map = mutableMapOf<String, Any>()
            val keys = jsonObject.keys()

            while (keys.hasNext()) {
                val key = keys.next()
                val value = jsonObject.get(key)
                map[key] = value
            }
            return map
        }

    }
}