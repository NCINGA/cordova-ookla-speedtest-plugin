package com.ncinga.speedtest


import com.ookla.speedtest.sdk.ConfigHandlerBase
import com.ookla.speedtest.sdk.MainThreadConfigHandler
import com.ookla.speedtest.sdk.SpeedtestResult
import com.ookla.speedtest.sdk.SpeedtestSDK
import com.ookla.speedtest.sdk.TaskManager
import com.ookla.speedtest.sdk.config.Config
import com.ookla.speedtest.sdk.config.ValidatedConfig
import com.ookla.speedtest.sdk.handler.TaskManagerController
import com.ookla.speedtest.sdk.handler.TestHandlerBase
import com.ookla.speedtest.sdk.model.LatencyResult
import com.ookla.speedtest.sdk.model.TransferResult
import com.ookla.speedtest.sdk.result.OoklaError
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.cordova.CallbackContext
import org.json.JSONArray
import org.json.JSONObject

class CustomTestHandler(
    private val speedtestSDK: SpeedtestSDK,
    private val configName: String,
    private val timeInterval: Int,
    private val endpoint: String,
    private val callbackContext: CallbackContext,
) : TestHandlerBase() {
    private var taskManager: TaskManager? = null
    private val TAG = "SpeedTest"
    private var endpointHandler: EndpointHandler? = null


    fun runTestWithSingleServer(
        count: Int,
        currentIteration: Int = 1,
        testResult: MutableList<ResultDTO> = mutableListOf()


    ) {
        if (currentIteration > count) {
            endpointHandler = EndpointHandler(endpoint)
            val jsonStringResult = convertTestResultsToJson(testResult)
            val result = convertTestResultsToJsonArray(testResult)
            endpointHandler?.sendData(jsonStringResult);
            Log.i("Final Result", result.toString())
            callbackContext.success(result)
            return
        }

        Log.i(TAG, "loop $currentIteration")
        Log.i(TAG, "execute runTestWithSingleServer()")
        val config = Config.newConfig(configName)

        val configHandler = object : ConfigHandlerBase() {
            override fun onConfigFetchFinished(validatedConfig: ValidatedConfig?) {
                val handler = object : TestHandlerBase() {
                    override fun onLatencyFinished(
                        taskController: TaskManagerController?,
                        result: LatencyResult
                    ) {
                        super.onLatencyFinished(taskController, result)
                        Log.d(TAG, "Latency Result: ${result}")
                        taskManager?.startNextStage()
                    }

                    override fun onUploadFinished(
                        taskController: TaskManagerController?,
                        result: TransferResult
                    ) {
                        super.onUploadFinished(taskController, result)
                        Log.d(TAG, "Upload Speed: ${result}")
                        taskManager?.startNextStage()
                    }

                    override fun onDownloadFinished(
                        taskController: TaskManagerController?,
                        result: TransferResult
                    ) {
                        super.onDownloadFinished(taskController, result)
                        Log.d(TAG, "Download Speed: ${result}")
                        taskManager?.startNextStage()
                    }

                    override fun onTestFinished(speedtestResult: SpeedtestResult) {
                        super.onTestFinished(speedtestResult)
                        val result = speedtestResult.getResult().toJsonString()
                        testResult.add(ResultDTO("loop$currentIteration", result))
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(timeInterval.toLong() * 1000)
                            runTestWithSingleServer(count, currentIteration + 1, testResult)
                        }
                    }

                    override fun onTestFailed(
                        error: OoklaError,
                        speedtestResult: SpeedtestResult?
                    ) {
                        super.onTestFailed(error, speedtestResult)
                        Log.e(TAG, error.message)
                        taskManager?.startNextStage()

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(timeInterval.toLong() * 1000)
                            runTestWithSingleServer(count, currentIteration + 1, testResult)
                        }
                    }
                }

                taskManager = speedtestSDK.newTaskManager(handler, validatedConfig)
                taskManager?.start()
            }

            override fun onConfigFetchFailed(error: OoklaError) {
                Log.e(TAG, "Config fetch failed with: ${error.message}")
                callbackContext.error("Config fetch failed: ${error.message}")

                CoroutineScope(Dispatchers.Main).launch {
                    delay(timeInterval.toLong() * 1000)
                    runTestWithSingleServer(count, currentIteration + 1, testResult)
                }
            }
        }

        ValidatedConfig.validate(config, MainThreadConfigHandler(configHandler))
    }
}
