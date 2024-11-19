package com.ncinga.speedtest

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


interface APIServiceInterface {
    fun sendResult(url: String, payload: JSONObject, headers: Headers?)
    fun getAuthToken(url: String, payload: JSONObject, headers: JSONObject): String
}

class APIService() : APIServiceInterface {
    private val client = OkHttpClient()
    private val TAG = "SpeedTest-APIService"

    override fun sendResult(url: String, payload: JSONObject, headers: Headers?) {
        if (url.isEmpty()) {
            Log.e(TAG, "Invalid URL.")
            return
        }

        val requestBody =
            payload.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(requestBody)
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

    override fun getAuthToken(url: String, payload: JSONObject, headers: JSONObject): String {
        if (url.isEmpty()) {
            Log.e(TAG, "Invalid URL.")
            return "Invalid URL."

        }
        val requestBody =
            payload.toString()
                .toRequestBody("application/x-www-form-urlencoded; charset=utf-8".toMediaTypeOrNull())
        val keyId = headers.optString("keyId")
        val request = Request.Builder().url(url).post(requestBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("KeyId", keyId)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Request sent successfully.")
                    response.body?.let {
                        val responseString = it.string()
                        Log.i(TAG, "Response: $responseString")
                        val jsonResponse = JSONObject(responseString)
                        println(jsonResponse)

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
        return "JJ"
    }
}
