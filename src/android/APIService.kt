package com.ncinga.speedtest

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


interface APIServiceInterface {
    fun sendData(url: String, body: String)
}

class APIService() : APIServiceInterface {
    private val client = OkHttpClient()
    private val TAG = "SpeedTest-APIService"

    override fun sendData(url: String, body: String) {
        if (url.isEmpty()) {
            Log.e(TAG, "Invalid URL.")
            return
        }

        val requestBody = body.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(requestBody)
            .addHeader("Content-Type", "application/json").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("EndpointHandler", "Data sent successfully.")
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
}
