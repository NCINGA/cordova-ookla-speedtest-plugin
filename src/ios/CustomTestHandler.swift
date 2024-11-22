import Foundation
import SpeedtestSDK
import os

public class CustomTestHandler {
    private var speedTestsdk: STSpeedtestSDK?
    private var configName: String?
    private var taskManager: STTaskManager?
    private var callback: (([String: Any]) -> Void)?
    private var count: Int32
    private var timeInterval: Int32?
    private var apiService: APIService?

    init(speedTestsdk: STSpeedtestSDK?, configName: String?, count: Int32 = 1, timeInterval: Int32?, callback: (([String: Any]) -> Void)?) {
        self.speedTestsdk = speedTestsdk
        self.configName = configName
        self.count = count
        self.callback = callback
        self.timeInterval = timeInterval
        self.apiService = APIService()
    }

    func runTestWithSingleServer(endpoint: String, clientId: String, keyId: String, clientSecret: String, grantType: String, providerOrgCode: String, tokenAPI: String) {
        guard count > 0 else {
            os_log("Invalid count. Cannot run test.")
            return
        }
        runTestIteration(currentIteration: 1, endpoint: endpoint, clientId: clientId, keyId: keyId, clientSecret: clientSecret, grantType: grantType, providerOrgCode: providerOrgCode, tokenAPI: tokenAPI)
    }

    private func runTestIteration(currentIteration: Int, endpoint: String, clientId: String, keyId: String, clientSecret: String, grantType: String, providerOrgCode: String, tokenAPI: String) {
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

            let handler = TestHandler(
                currentIteration: currentIteration,
                endpoint: endpoint,
                clientId: clientId,
                keyId: keyId,
                clientSecret: clientSecret,
                grantType: grantType,
                providerOrgCode: providerOrgCode,
                tokenAPI: tokenAPI,
                customTestHandler: self,
                completionHandler: {
                    let delay = Double(self.timeInterval ?? 10)
                    DispatchQueue.main.asyncAfter(deadline: .now() + delay) {
                        self.runTestIteration(
                            currentIteration: currentIteration + 1,
                            endpoint: endpoint,
                            clientId: clientId,
                            keyId: keyId,
                            clientSecret: clientSecret,
                            grantType: grantType,
                            providerOrgCode: providerOrgCode,
                            tokenAPI: tokenAPI
                        )
                    }
                }
            )

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
        let message: [String: Any] = ["status": "All tests completed."]
        callback?(message)
    }


    class TestHandler: NSObject, STTestHandlerDelegate {
        weak var taskManager: STTaskManager?
        private var currentIteration: Int
        private var endpoint: String
        private var clientId: String
        private var keyId: String
        private var clientSecret: String
        private var grantType: String
        private var providerOrgCode: String
        private var tokenAPI: String
        private var customTestHandler: CustomTestHandler
        private var completionHandler: () -> Void

        init(currentIteration: Int, endpoint: String, clientId: String, keyId: String, clientSecret: String, grantType: String, providerOrgCode: String, tokenAPI: String, customTestHandler: CustomTestHandler, completionHandler: @escaping () -> Void) {
            self.currentIteration = currentIteration
            self.endpoint = endpoint
            self.clientId = clientId
            self.keyId = keyId
            self.clientSecret = clientSecret
            self.grantType = grantType
            self.providerOrgCode = providerOrgCode
            self.tokenAPI = tokenAPI
            self.customTestHandler = customTestHandler
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
                    os_log("Result for loop %d: %@", currentIteration, jsonString)

                    customTestHandler.sendUpdate(
                        endpoint: endpoint,
                        clientId: clientId,
                        keyId: keyId,
                        clientSecret: clientSecret,
                        grantType: grantType,
                        providerOrgCode: providerOrgCode,
                        result: jsonString,
                        tokenAPI: tokenAPI,
                        callback: { pluginResult in
                            os_log("Plugin result received: %@", "\(pluginResult)")
                        }
                    )
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

    private func sendUpdate(
        endpoint: String,
        clientId: String,
        keyId: String,
        clientSecret: String,
        grantType: String,
        providerOrgCode: String,
        result: String,
        tokenAPI: String,
        callback: @escaping ([String: Any]) -> Void
    ) {
        let pluginResult: [String: Any] = ["status": "OK", "result": result]

        if apiService == nil {
            apiService = APIService()
        }

        let payload: [String: String] = [
            "client_id": clientId,
            "client_secret": clientSecret,
            "grant_type": grantType
        ]

        let uuid = UUID().uuidString
        let header: [String: String] = [
            "keyId": keyId
        ]

        apiService?.getAuthToken(url: tokenAPI, payload: payload, headers: header) { token in
            guard let token = token else {
                os_log("Failed to fetch token.")
                return
            }

            let headers: [String: String] = [
                "providerOrgCode": providerOrgCode,
                "transactionId": "API-REST-\(uuid)",
                "timestamp": self.generateManualTimestamp(),
                "keyId": keyId,
                "token": "Bearer \(token)"
            ]

            self.apiService?.sendResult(url: endpoint, payload: result, headers: headers)

            callback(pluginResult)
        }
    }
    private func generateManualTimestamp() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX"
        formatter.timeZone = TimeZone.current
        return formatter.string(from: Date())
    }
}
