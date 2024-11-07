package com.ncinga.speedtest

import android.util.Log
import org.apache.cordova.*
import org.json.JSONArray
import org.json.JSONObject
import com.ookla.speedtest.sdk.SpeedtestSDK

class SpeedTest : CordovaPlugin() {
    private val TAG = "SpeedTest"
    private var customTestHandler: CustomTestHandler? = null
    private var speedtestSDK: SpeedtestSDK? = null

    override fun execute(
        action: String,
        args: JSONArray,
        callbackContext: CallbackContext
    ): Boolean {
        return when (action) {
            "startTesting" -> {
                val jsonObject = parseAndValidateJson(args, callbackContext) ?: return false
                handleStartTesting(jsonObject, callbackContext)
            }

            "stopTesting" -> {
                customTestHandler?.stopTesting()
                return true;
            }

            else -> false
        }
    }

    private fun parseAndValidateJson(
        args: JSONArray,
        callbackContext: CallbackContext
    ): JSONObject? {
        val jsonString = args.optString(0)
        if (jsonString.isNullOrEmpty()) {
            Log.e(TAG, "First argument is not a valid JSON string.")
            callbackContext.error("First argument is not a valid JSON string.")
            return null
        }

        return try {
            JSONObject(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON: ${e.message}")
            callbackContext.error("Error parsing JSON: ${e.message}")
            null
        }
    }

    private fun handleStartTesting(
        jsonObject: JSONObject,
        callbackContext: CallbackContext
    ): Boolean {
        val apiKey = jsonObject.optString("apiKey")
        val config = jsonObject.optString("config")
        val endpoint = jsonObject.optString("endpoint")
        val count = jsonObject.optInt("count", 1)
        val timeInterval = jsonObject.optInt("timeInterval", 10)

        if (apiKey.isEmpty()) {
            Log.e(TAG, "API Key is empty")
            callbackContext.error("API Key is required")
            return false
        }
        if (config.isEmpty()) {
            Log.e(TAG, "Config is required")
            callbackContext.error("Config is required")
            return false
        }
        if (endpoint.isEmpty()) {
            Log.e(TAG, "Endpoint is required")
            callbackContext.error("Endpoint is required")
            return false
        }

        cordova.activity.runOnUiThread {
            try {
                speedtestSDK = SpeedtestSDK.initSDK(cordova.activity.application, apiKey)
                customTestHandler = CustomTestHandler(
                    speedtestSDK!!,
                    config,
                    timeInterval,
                    endpoint,
                    callbackContext
                )
                Log.i(TAG, "Config: $config, Endpoint: $endpoint, Count: $count")
                customTestHandler?.runTestWithSingleServer(count)
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing SpeedtestSDK: ${e.message}")
                callbackContext.error("Error initializing SpeedtestSDK: ${e.message}")
            }
        }
        return true
    }


}
