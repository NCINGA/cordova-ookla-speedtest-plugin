package com.ncinga.speedtest

import android.util.Log
import org.apache.cordova.*
import org.json.JSONArray
import org.json.JSONObject
import com.ookla.speedtest.sdk.SpeedtestSDK

class SpeedTest : CordovaPlugin() {
    private val TAG = "SpeedTest-SpeedTest"
    private var customTestHandler: CustomTestHandler? = null
    private var speedtestSDK: SpeedtestSDK? = null

    override fun execute(
        action: String, args: JSONArray, callbackContext: CallbackContext
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
        args: JSONArray, callbackContext: CallbackContext
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
        jsonObject: JSONObject, callbackContext: CallbackContext
    ): Boolean {
        val apiKey = jsonObject.optString("apiKey")
        val config = jsonObject.optString("config")
        val endpoint = jsonObject.optString("endpoint")
        val clientId = jsonObject.optString("clientId")
        val keyId = jsonObject.optString("keyId")
        val clientSecret = jsonObject.optString("clientSecret")
        val grantType = jsonObject.optString("grantType")
        val providerOrgCode = jsonObject.optString("providerOrgCode")
        val tokenAPI = jsonObject.optString("tokenApi")


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

        if (clientId.isEmpty()) {
            Log.e(TAG, "Client id is required")
            callbackContext.error("Client id is required")
            return false
        }

        if (keyId.isEmpty()) {
            Log.e(TAG, "KeyId is required")
            callbackContext.error("KeyId is required")
            return false
        }

        if (clientSecret.isEmpty()) {
            Log.e(TAG, "Client Security is required")
            callbackContext.error("Client Security is required")
            return false
        }

        if (grantType.isEmpty()) {
            Log.e(TAG, "Grant Type is required")
            callbackContext.error("Grant Type is required")
            return false
        }

        if (providerOrgCode.isEmpty()) {
            Log.e(TAG, "Provider Org Code is required")
            callbackContext.error("Provider Org Code is required")
            return false
        }

        if (tokenAPI.isEmpty()) {
            Log.e(TAG, "Token API is required")
            callbackContext.error("Token API is required")
            return false
        }


        cordova.activity.runOnUiThread {
            try {

                speedtestSDK = SpeedtestSDK.initSDK(cordova.activity.application, apiKey)
                customTestHandler = CustomTestHandler(
                    speedtestSDK!!, config, callbackContext
                )
                customTestHandler?.runSpeedTestTask(
                    endpoint,
                    clientId,
                    keyId,
                    clientSecret,
                    grantType,
                    providerOrgCode,
                    tokenAPI
                )
                Log.i(
                    TAG,
                    "Config Name: $config, Endpoint URL: $endpoint, Client Id $clientId, Key Id $keyId, Client Security, $clientSecret, Grant Type $grantType, Provider Org Code $providerOrgCode, Token API : $tokenAPI"
                )


            } catch (e: Exception) {
                Log.e(TAG, "Error initializing SpeedtestSDK: ${e.message}")
                callbackContext.error("Error initializing SpeedtestSDK: ${e.message}")
            }
        }
        return true
    }


}
