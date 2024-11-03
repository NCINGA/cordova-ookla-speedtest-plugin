import Foundation
import SpeedtestSDK
import CoreLocation
import os

public class CustomTestHandler {
    private var testResult: [ResultDTO] = []
    private var speedTestsdk: STSpeedtestSDK?
    private var configName: String?
    private var taskManager: STTaskManager?
    private var callback: (([String: Any]) -> Void)?
    private var count: Int32
    private var endpointHandler: EndpointHandler?
    private var timeInterval: Int32?

    init(speedTestsdk: STSpeedtestSDK?, configName: String?, count: Int32 = 1,
         endpoint: String?, timeInterval: Int32?, callback: (([String: Any]) -> Void)?) {
        self.speedTestsdk = speedTestsdk
        self.configName = configName
        self.count = count
        self.callback = callback
        self.timeInterval = timeInterval
        self.endpointHandler = EndpointHandler(endpoint: endpoint ?? "")
    }

    func runTestWithSingleServer() {
        guard count > 0 else {
            os_log("Invalid count. Cannot run test.")
            return
        }

        runTestIteration(currentIteration: 1)
    }

    private func runTestIteration(currentIteration: Int) {
        guard currentIteration <= count else {
            finalizeResults()
            return
        }

        os_log("Running test iteration %d of %d", currentIteration, count)

        let config = STConfig.newConfig(configName ?? "SpeedTest")
        config?.validate { [weak self] validatedConfig, error in
            guard let self = self, let validatedConfig = validatedConfig else {
                os_log("Failed to load config: %@", error?.message ?? "Unknown Error")
                return
            }

            let handler = TestHandler(currentIteration: currentIteration, testResult: &self.testResult) {
                let delay = Double(self.timeInterval ?? 10)
                DispatchQueue.main.asyncAfter(deadline: .now() + delay) {
                    self.runTestIteration(currentIteration: currentIteration + 1)
                }
            }

            do {
                self.taskManager = try self.speedTestsdk?.newTaskManager(STMainThreadTestHandler(delegate: handler), config: validatedConfig)
                handler.taskManager = self.taskManager
                self.taskManager?.start()
            } catch {
                os_log("Failed to create task manager: %@", "\(error)")
            }
        }
    }

    private func finalizeResults() {
        os_log("All tests completed.")
        do {
            guard let testResults = testResult as? [ResultDTO] else {
                os_log("testResult is not of type [ResultDTO].")
                return
            }
            let resultArray = convertTestResultsToJsonArray(testResults: testResults)
            self.endpointHandler?.sendData(body: convertTestResultsToJson(testResults: testResults))
            callback?(resultArray)
        } catch {
            os_log("Error processing result: \(error.localizedDescription)")
        }
    }

    class TestHandler: NSObject, STTestHandlerDelegate, ObservableObject {
        weak var taskManager: STTaskManager?
        private var currentIteration: Int
        private var completionHandler: () -> Void
        private var testResult: UnsafeMutablePointer<[ResultDTO]>

        init(currentIteration: Int, testResult: inout [ResultDTO], completionHandler: @escaping () -> Void) {
            self.currentIteration = currentIteration
            self.testResult = UnsafeMutablePointer(&testResult)
            self.completionHandler = completionHandler
        }

        func onResultUploadFinished(result: SpeedtestSDK.STResult.ResultUpload?, _ error: SpeedtestSDK.STResult.OoklaError?) {
            taskManager?.startNextStage()
        }

        func onUploadFinished(_ taskController: STTaskManagerController?, result: STTransferResult) {
            taskManager?.startNextStage()
        }

        func onLatencyFinished(_ taskController: STTaskManagerController?, result: STLatencyResult) {
            taskManager?.startNextStage()
        }

        func onDownloadFinished(_ taskController: STTaskManagerController?, result: STTransferResult) {
            taskManager?.startNextStage()
        }

        func onTestFinished(_ result: SpeedtestSDK.STSpeedtestResult) {
            do {
                let jsonData = try JSONEncoder().encode(result.getResult())
                if let jsonString = String(data: jsonData, encoding: .utf8) {
                    let loopResult = ResultDTO(id: "loop\(currentIteration)", result: jsonString)
                    testResult.pointee.append(loopResult)
                } else {
                    os_log("Error converting JSON data to string.")
                }
            } catch {
                os_log("Failed to get JSON result: %@", error.localizedDescription)
            }
            taskManager?.startNextStage()
            completionHandler()
        }
    }
}
