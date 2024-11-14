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
    private var apiService: APIService? = null
    private lateinit var apiKey: String
    private lateinit var config: String
    private lateinit var endpoint: String
    private var count: Int = 0
    private var timeInterval: Int = 0
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
        val configFetchURL = jsonObject.optString("configFetchURL", null)
        if (configFetchURL.isNullOrEmpty()) {
            callbackContext.error("Config URL not provided")
            return false
        }

        cordova.activity.runOnUiThread {
            try {
                val app = cordova.activity.application
                apiService = APIService()

                apiService?.fetchConfiguration(configFetchURL) { jsonConfig ->
                    if (jsonConfig != null) {
                        Log.i(TAG, "Config: $jsonConfig")
                        apiKey = jsonConfig.optString("apiKey")
                        config = jsonConfig.optString("config")
                        endpoint = jsonConfig.optString("endpoint")
                        count = jsonConfig.optInt("count", 1)

                        val timeIntervalConfig = jsonConfig.optJSONObject("timeInterval")
                        timeInterval = if (timeIntervalConfig != null) {
                            val hours = timeIntervalConfig.optInt("hours", 0)
                            val minutes = timeIntervalConfig.optInt("minutes", 0)
                            val seconds = timeIntervalConfig.optInt("seconds", 0)
                            (hours * 60 * 60) + (minutes * 60) + seconds
                        } else {
                            60
                        }

                        if (apiKey.isEmpty()) {
                            Log.e(TAG, "API Key is empty")
                            callbackContext.error("API Key is required")
                            return@fetchConfiguration
                        }
                        if (config.isEmpty()) {
                            Log.e(TAG, "Config is required")
                            callbackContext.error("Config is required")
                            return@fetchConfiguration
                        }
                        if (endpoint.isEmpty()) {
                            Log.e(TAG, "Endpoint is required")
                            callbackContext.error("Endpoint is required")
                            return@fetchConfiguration
                        }
                        cordova.activity.runOnUiThread {
                            try {
                                speedtestSDK = SpeedtestSDK.initSDK(app, apiKey)
                                customTestHandler = CustomTestHandler(
                                    speedtestSDK!!,
                                    config,
                                    timeInterval,
                                    apiService!!,
                                    callbackContext
                                )

                                Log.i(
                                    TAG,
                                    "[ConfigName: $config, Endpoint: $endpoint, Count: $count, TimeInterval: $timeInterval]"
                                )
                                customTestHandler?.runTestWithSingleServer(count, 1, endpoint)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error initializing SpeedtestSDK: ${e.message}")
                                callbackContext.error("Error initializing SpeedtestSDK: ${e.message}")
                            }
                        }
                    } else {
                        Log.e(TAG, "Config not found or error occurred.")
                        callbackContext.error("Config not found or error occurred.")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing SpeedtestSDK: ${e.message}")
                callbackContext.error("Error initializing SpeedtestSDK: ${e.message}")
            }
        }
        return true
    }


}
