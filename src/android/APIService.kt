package com.ncinga.speedtest

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


interface APIServiceInterface {
    fun sendResult(url: String, payload: JSONObject, headers: JSONObject?)
    fun getAuthToken(
        url: String,
        payload: JSONObject,
        headers: JSONObject,
        callback: (String) -> Unit
    )
}


class APIService() : APIServiceInterface {
    private val client = OkHttpClient()
    private val TAG = "SpeedTest-APIService"

    override fun sendResult(url: String, payload: JSONObject, headers: JSONObject?) {
        if (url.isEmpty()) {
            Log.e(TAG, "Invalid URL.")
            return
        }

        val requestBody =
            payload.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val token = headers?.optString("token", "")
        val providerOrgCode = headers?.optString("providerOrgCode", "")
        val transactionId = headers?.optString("transactionId", "")
        val keyId = headers?.optString("keyId", "")
        val timestamp = headers?.optString("timestamp", "")
        val request = Request.Builder().url(url).post(requestBody)
            .addHeader("Token", token!!)
            .addHeader("ProviderOrgCode", providerOrgCode!!)
            .addHeader("KeyId", keyId!!)
            .addHeader("Timestamp", timestamp!!)
            .addHeader("TransactionId", transactionId!!)
            .addHeader("Content-Type", "application/json").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Data sent successfully.")
                    response.body?.let {
                        val responseString = it.string()
                        Log.i(TAG, "Response: $responseString")
                    }
                } else {
                    Log.e(
                        TAG,
                        "Failed to send data or server error. Status code: ${response.code}"
                    )
                }
                response.close()
            }
        })
    }


    override fun getAuthToken(
        url: String,
        payload: JSONObject,
        headers: JSONObject,
        callback: (String) -> Unit
    ) {
        Thread {
            if (url.isEmpty()) {
                callback("Invalid URL.")
                return@Thread
            }

            val formBodyBuilder = FormBody.Builder()
            payload.keys().forEach { key ->
                payload.optString(key)?.let { formBodyBuilder.add(key, it) }
            }
            val requestBody = formBodyBuilder.build()

            val keyId = headers.optString("KeyId", "")
            if (keyId.isEmpty()) {
                callback("Invalid headers.")
                return@Thread
            }

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("KeyId", keyId)
                .build()

            try {
                val response = client.newCall(request).execute()
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: "Empty response body."
                        val jsonResponse = JSONObject(responseBody)
                        callback(jsonResponse.optString("access_token", "Token not found."))
                    } else {
                        callback("Error: ${response.code} - ${response.message}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback("Exception occurred: ${e.message}")
            }
        }.start()
    }


}
