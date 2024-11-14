package com.ncinga.speedtest

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

fun interface ConfigCallback {
    fun onConfigFetched(jsonConfig: JSONObject?)
}

interface APIServiceInterface {
    fun sendData(url: String, body: String)
    fun fetchConfiguration(url: String, callback: ConfigCallback)
}

class APIService() : APIServiceInterface {
    private val client = OkHttpClient()
    private val TAG = "SpeedTest"

    override fun sendData(url: String, body: String) {
        if (url.isEmpty()) {
            Log.e("EndpointHandler", "Invalid URL.")
            return
        }

        val requestBody = body.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(requestBody)
            .addHeader("Content-Type", "application/json").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("EndpointHandler", "Error: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("EndpointHandler", "Data sent successfully.")
                    response.body?.let {
                        val responseString = it.string()
                        Log.i("EndpointHandler", "Response: $responseString")
                    }
                } else {
                    Log.e(
                        "EndpointHandler",
                        "Failed to send data or server error. Status code: ${response.code}"
                    )
                }
                response.close()
            }
        })
    }

    override fun fetchConfiguration(
        url: String, callback: ConfigCallback
    ) {
        Log.i(TAG, "Fetch Config.....")

        if (url.isEmpty()) {
            Log.e("EndpointHandler", "Invalid URL.")
            callback.onConfigFetched(null)
            return
        }

        val request =
            Request.Builder().url(url).get().addHeader("Content-Type", "application/json").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("EndpointHandler", "Error: ${e.localizedMessage}")
                callback.onConfigFetched(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonConfig: JSONObject? = if (response.isSuccessful) {
                    response.body?.let {
                        val responseString = it.string()
                        try {
                            JSONObject(responseString)
                        } catch (e: JSONException) {
                            Log.e(TAG, "Config not found")
                            null
                        }
                    }
                } else {
                    null
                }
                val config = jsonConfig?.optJSONObject("config")
                callback.onConfigFetched(config)
                response.close()
            }
        })

    }

}
