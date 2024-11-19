package com.ncinga.speedtest

import android.util.Log
import com.ookla.speedtest.sdk.*
import com.ookla.speedtest.sdk.config.Config
import com.ookla.speedtest.sdk.config.Task
import com.ookla.speedtest.sdk.config.ValidatedConfig
import com.ookla.speedtest.sdk.handler.TaskManagerController
import com.ookla.speedtest.sdk.handler.TestHandlerBase
import com.ookla.speedtest.sdk.model.LatencyResult
import com.ookla.speedtest.sdk.model.PacketlossResult
import com.ookla.speedtest.sdk.model.ThroughputResult
import com.ookla.speedtest.sdk.model.ThroughputStage
import com.ookla.speedtest.sdk.model.TransferResult
import com.ookla.speedtest.sdk.result.OoklaError
import com.ookla.speedtest.sdk.result.ResultUpload
import com.ookla.speedtest.sdk.result.Traceroute
import com.ookla.speedtest.sdk.result.TracerouteHop
import org.apache.cordova.CallbackContext
import org.apache.cordova.PluginResult
import org.json.JSONObject

class CustomTestHandler(
    private val speedtestSDK: SpeedtestSDK,
    private val configName: String,
    private var endpointURL: String,
    private val callbackContext: CallbackContext

) : TestHandlerBase() {
    private var taskManager: TaskManager? = null
    private val TAG = "SpeedTest-CustomTestHandler"
    private var testFinished: Boolean = false
    private var apiService: APIService? = null

    fun runSpeedTestTask() {
        val config = Config.newConfig(configName)
        if (apiService == null) {
            apiService = APIService();
        }
        config?.tasks = arrayListOf(Task.newThroughputTask())

        var backgroundThroughputTaskManager: TaskManager?
        val configHandler = object : ConfigHandlerBase() {
            override fun onConfigFetchFinished(validatedConfig: ValidatedConfig?) {
                if (testFinished) {
                    return
                }
                val handler = object : TestHandlerBase() {

                    override fun onTestStarted(taskController: TaskManagerController?) {
                        Log.i(TAG, "Start Testing...")
                    }

                    override fun onLatencyFinished(
                        taskController: TaskManagerController?, result: LatencyResult
                    ) {
                        Log.i(TAG, "Latency Result: $result")
                    }

                    override fun onUploadProgressUpdated(
                        result: TransferResult, progressPercentage: Float
                    ) {
                        val speed = String.format("%.3f", result.speedMbps)
                        Log.i(TAG, "Upload Progress: $speed Mbps")
                    }

                    override fun onResultUploadFinished(
                        resultUpload: ResultUpload?, error: OoklaError?
                    ) {
                        if (error == null && resultUpload?.didSucceed == true) {
                            Log.i(TAG, "Result Upload Success: ${resultUpload.httpStatusCode}")
                        } else {
                            Log.e(TAG, "Result Upload Failure: ${resultUpload?.httpStatusCode}")
                            resultUpload?.response?.error?.forEach {
                                Log.e(TAG, "Error: $it")
                            }
                        }
                    }

                    override fun onUploadFinished(
                        taskController: TaskManagerController?, result: TransferResult
                    ) {
                        val speed = String.format("%.3f", result.speedMbps)
                        Log.i(TAG, "Upload Speed: $speed Mbps")
                    }

                    override fun onDownloadFinished(
                        taskController: TaskManagerController?, result: TransferResult
                    ) {
                        Log.i(TAG, "Download Speed: ${result.speedMbps} Mbps")
                    }

                    override fun onTestFinished(speedtestResult: SpeedtestResult) {
                        val resultJson = speedtestResult.getResult().toJsonString()
                        val result = speedtestResult.getResult().toJsonString()
                        sendUpdate(endpointURL, result)
                        Log.i(TAG, "Test Finished: $resultJson")
                    }

                    override fun onTestFailed(
                        error: OoklaError, speedtestResult: SpeedtestResult?
                    ) {
                        Log.e(TAG, "Test Failed: ${error.message}")
                    }

                    override fun onThroughputStageStarted(
                        taskController: TaskManagerController?, stage: ThroughputStage
                    ) {
                        Log.i(TAG, "Throughput Stage Started: $stage")
                    }

                    override fun onThroughputStageFailed(
                        error: OoklaError, stage: ThroughputStage, result: SpeedtestResult?
                    ) {
                        Log.e(TAG, "Throughput Stage Failed: $error")
                    }

                    override fun onThroughputStageFinished(
                        taskController: TaskManagerController?, stage: ThroughputStage
                    ) {
                        Log.i(TAG, "Throughput Stage Finished")
                    }

                    override fun onThroughputTaskStarted(
                        taskController: TaskManagerController?, remoteIp: String, localIp: String
                    ) {
                        Log.i(TAG, "Throughput Task Started")
                    }

                    override fun onThroughputTaskFinished(
                        taskController: TaskManagerController?, result: ThroughputResult
                    ) {
                        Log.i(TAG, "Throughput Task Finished")
                    }

                    override fun onPacketlossFinished(
                        taskController: TaskManagerController?, result: PacketlossResult
                    ) {
                        Log.i(
                            TAG,
                            "Packet Loss Finished - Sent: ${result.packetsSent}, Received: ${result.packetsReceived}"
                        )
                    }

                    override fun onLatencyProgressUpdated(
                        result: LatencyResult, progressPercentage: Float
                    ) {
                        val latency = String.format("%.3f", result.latencyMillis / 1000.0)
                        Log.i(TAG, "Latency Progress Updated: $latency")
                    }

                    override fun onDownloadProgressUpdated(
                        result: TransferResult, progressPercentage: Float
                    ) {
                        val speed = String.format("%.3f", result.speedMbps)
                        Log.i(TAG, "Download Progress: $speed Mbps")
                    }

                    override fun onTracerouteStarted(
                        taskController: TaskManagerController?, host: String, ip: String
                    ) {
                        Log.i(TAG, "Traceroute Started")
                    }

                    override fun onTracerouteHop(host: String, hop: TracerouteHop) {
                        Log.i(TAG, "Traceroute Hop - Host: $host, Hop: $hop")
                    }

                    override fun onTracerouteFinished(
                        taskController: TaskManagerController?, host: String, traceroute: Traceroute
                    ) {
                        Log.i(TAG, "Traceroute Finished")
                    }

                    override fun onTracerouteFailed(
                        error: OoklaError, host: String, traceroute: Traceroute?
                    ) {
                        Log.e(TAG, "Traceroute Failed: ${error.message}")
                    }

                    override fun onTracerouteCanceled(host: String) {
                        Log.i(TAG, "Traceroute Canceled")
                    }

                    override fun onDeviceStateCaptureFinished(result: BackgroundScanResult) {
                        Log.i(
                            TAG,
                            "Device State Capture Finished: ${result.getResult().toJsonString()}"
                        )
                        taskManager?.startNextStage()
                    }
                }
                Log.i(
                    TAG,
                    "Config retrieved over connection type ${validatedConfig?.connectionType.toString()}"
                )

                val backgroundThroughputTaskManagerStatus =
                    speedtestSDK.newTaskManagerWithAutoAdvance(handler, validatedConfig)
                backgroundThroughputTaskManager = backgroundThroughputTaskManagerStatus.taskManager

                if (backgroundThroughputTaskManagerStatus.didExist()) {
                    Log.i(TAG, "Background task is already running.")

                } else {
                    backgroundThroughputTaskManager?.start()
                    Log.i(TAG, "Started background task.")
                }
            }

            override fun onConfigFetchFailed(error: OoklaError) {
                Log.e(TAG, "Config fetch failed with ${error.message}")
            }
        }

        ValidatedConfig.validate(config, MainThreadConfigHandler(configHandler))
    }

    private fun sendUpdate(endpointURL: String, result: String) {
        val pluginResult = PluginResult(PluginResult.Status.OK, result)
        pluginResult.keepCallback = true
        apiService?.sendData(endpointURL, result)
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
