package com.ncinga.speedtest

import android.util.Log
import com.ookla.speedtest.sdk.*
import com.ookla.speedtest.sdk.config.Config
import com.ookla.speedtest.sdk.config.ValidatedConfig
import com.ookla.speedtest.sdk.handler.TaskManagerController
import com.ookla.speedtest.sdk.handler.TestHandlerBase
import com.ookla.speedtest.sdk.model.LatencyResult
import com.ookla.speedtest.sdk.model.TransferResult
import com.ookla.speedtest.sdk.result.OoklaError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.cordova.CallbackContext
import org.apache.cordova.PluginResult
import org.json.JSONObject

class CustomTestHandler(
    private val speedtestSDK: SpeedtestSDK,
    private val configName: String,
    private val timeInterval: Int,
    private val endpoint: String,
    private val callbackContext: CallbackContext

) : TestHandlerBase() {
    private var taskManager: TaskManager? = null
    private val TAG = "SpeedTest"
    private var endpointHandler: EndpointHandler? = null
    fun runTestWithSingleServer(
        count: Int,
        currentIteration: Int = 1
    ) {
        endpointHandler = EndpointHandler(endpoint)
        if (count != -1 && currentIteration > count) {
            Log.i(TAG, "All Test completed")
            return
        }

        Log.i(TAG, "Iteration $currentIteration")
        val config = Config.newConfig(configName)

        val configHandler = object : ConfigHandlerBase() {
            override fun onConfigFetchFinished(validatedConfig: ValidatedConfig?) {
                val handler = object : TestHandlerBase() {
                    override fun onLatencyFinished(
                        taskController: TaskManagerController?,
                        result: LatencyResult
                    ) {
                        super.onLatencyFinished(taskController, result)
                        Log.d(TAG, "Latency Result: $result")
                        taskManager?.startNextStage()
                    }

                    override fun onUploadFinished(
                        taskController: TaskManagerController?,
                        result: TransferResult
                    ) {
                        super.onUploadFinished(taskController, result)
                        Log.d(TAG, "Upload Speed: $result")
                        taskManager?.startNextStage()
                    }

                    override fun onDownloadFinished(
                        taskController: TaskManagerController?,
                        result: TransferResult
                    ) {
                        super.onDownloadFinished(taskController, result)
                        Log.d(TAG, "Download Speed: $result")
                        taskManager?.startNextStage()
                    }

                    override fun onTestFinished(speedtestResult: SpeedtestResult) {
                        super.onTestFinished(speedtestResult)
                        val result = speedtestResult.getResult().toJsonString()
                        sendUpdate(result, currentIteration)

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(timeInterval.toLong() * 1000)
                            runTestWithSingleServer(count, currentIteration + 1)
                        }
                    }

                    override fun onTestFailed(
                        error: OoklaError,
                        speedtestResult: SpeedtestResult?
                    ) {
                        super.onTestFailed(error, speedtestResult)
                        Log.e(TAG, error.message)
                        sendError(error.message ?: "Unknown error")

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(timeInterval.toLong() * 1000)
                            runTestWithSingleServer(count, currentIteration + 1)
                        }
                    }
                }

                taskManager = speedtestSDK.newTaskManager(handler, validatedConfig)
                taskManager?.start()
            }

            override fun onConfigFetchFailed(error: OoklaError) {
                Log.e(TAG, "Config fetch failed with: ${error.message}")
                sendError("Config fetch failed: ${error.message}")
            }
        }

        ValidatedConfig.validate(config, MainThreadConfigHandler(configHandler))
    }

    private fun sendUpdate(result: String, iteration: Int) {
        val jsonObject = JSONObject()
        jsonObject.put("iteration", iteration)
        val jsonResult = JSONObject(result)
        jsonObject.put("result", jsonResult)
        val pluginResult = PluginResult(PluginResult.Status.OK, jsonObject)
        pluginResult.keepCallback = true
        endpointHandler?.sendData(jsonObject.toString())
        callbackContext.sendPluginResult(pluginResult)
    }

    private fun sendError(message: String) {
        val pluginResult = PluginResult(PluginResult.Status.ERROR, message)
        pluginResult.keepCallback = false
        callbackContext.sendPluginResult(pluginResult)
    }

    fun stopTesting() {
        val result = JSONObject();
        result.put("message", "stop testing")
        taskManager?.cancel()
        val pluginResult = PluginResult(PluginResult.Status.OK, result)
        pluginResult.keepCallback = true
        callbackContext.sendPluginResult(pluginResult)
    }
}
