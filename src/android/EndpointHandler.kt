package com.ncinga.speedtest
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class EndpointHandler(private val endpoint: String) {
    private val client = OkHttpClient()
    fun sendData(body: String) {
        val url = endpoint
        if (url.isEmpty()) {
            Log.e("EndpointHandler", "Invalid URL.")
            return
        }

        val requestBody = body.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

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
                    Log.e("EndpointHandler", "Failed to send data or server error. Status code: ${response.code}")
                }
                response.close()
            }
        })
    }
}
