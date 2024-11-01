import SpeedtestSDK
import os

@objc(SpeedTest)
class SpeedTest: CDVPlugin {
    
    var sdk: STSpeedtestSDK?
    var customTestHandler: CustomTestHandler?
    
    func initializeSDK(apiKey: String) {
        os_log("Initializing SDK...")
        sdk = STSpeedtestSDK.shared
        do {
            try sdk?.initSDK(apiKey)
        } catch {
            os_log("Failed to initialize SDK: %@", "\(error)")
        }
    }
    
    func sendError(message: String, callbackId: String) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: message)
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }

    @objc(startTesting:)
    func startTesting(command: CDVInvokedUrlCommand) {
        
        guard let jsonString = command.arguments.first as? String,
              let jsonData = jsonString.data(using: .utf8) else {
            sendError(message: "First argument is not a valid JSON string.", callbackId: command.callbackId)
            return
        }
        
        do {
            guard let jsonObject = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: Any] else {
                sendError(message: "Invalid JSON format.", callbackId: command.callbackId)
                return
            }
            
            guard let apiKey = jsonObject["apiKey"] as? String, !apiKey.isEmpty else {
                sendError(message: "API Key is required", callbackId: command.callbackId)
                return
            }
            
            guard let config = jsonObject["config"] as? String, !config.isEmpty else {
                sendError(message: "Config is required", callbackId: command.callbackId)
                return
            }
            
            guard let endpoint = jsonObject["endpoint"] as? String, !endpoint.isEmpty else {
                sendError(message: "Endpoint is required", callbackId: command.callbackId)
                return
            }
            
            let count = jsonObject["count"] as? Int32 ?? 1

            if sdk == nil {
                initializeSDK(apiKey: apiKey)
            }
            
            guard let sdk = sdk else {
                sendError(message: "SDK initialization failed.", callbackId: command.callbackId)
                return
            }
            
            customTestHandler = CustomTestHandler(speedTestsdk: sdk, configName: config, count: count, endpoint: endpoint) { result in
                let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: result)
                print(result)
                self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
            }
            
            customTestHandler?.runTestWithSingleServer()
            
            let successMessage = "Speed test started successfully."
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: successMessage)
            self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
            
        } catch {
            sendError(message: "Error parsing JSON: \(error.localizedDescription)", callbackId: command.callbackId)
        }
    }
}
