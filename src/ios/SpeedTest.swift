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
            os_log("First argument is not a valid JSON string.")
            return
        }
        
        do {
            guard let jsonObject = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: Any] else {
                sendError(message: "Invalid JSON format.", callbackId: command.callbackId)
                os_log("Invalid JSON format.")
                return
            }
            
            guard let apiKey = jsonObject["apiKey"] as? String, !apiKey.isEmpty else {
                sendError(message: "API Key is required", callbackId: command.callbackId)
                os_log("API Key is required.")
                return
            }
            
            guard let config = jsonObject["config"] as? String, !config.isEmpty else {
                sendError(message: "Config is required", callbackId: command.callbackId)
                os_log("Config is required.")
                return
            }
            
            guard let endpoint = jsonObject["endpoint"] as? String, !endpoint.isEmpty else {
                sendError(message: "Endpoint is required", callbackId: command.callbackId)
                os_log("Endpoint is required.")
                return
            }
            
            let count = jsonObject["count"] as? Int32 ?? 1

            let timeInterval = jsonObject["timeInterval"] as? Int32 ?? 10
            
            guard let clientId = jsonObject["clientId"] as? String, !endpoint.isEmpty else {
                sendError(message: "Client id is required", callbackId: command.callbackId)
                os_log("Client id is required.")
                return
            }
            
            guard let keyId = jsonObject["keyId"] as? String, !endpoint.isEmpty else {
                sendError(message: "KeyId is required", callbackId: command.callbackId)
                os_log("KeyId is required.")
                return
            }
            
            guard let clientSecret = jsonObject["clientSecret"] as? String, !endpoint.isEmpty else {
                sendError(message: "Client Security is required", callbackId: command.callbackId)
                os_log("Client Security is required.")
                return
            }
            
            guard let grantType = jsonObject["grantType"] as? String, !endpoint.isEmpty else {
                sendError(message: "Grant Type is required", callbackId: command.callbackId)
                os_log("Grant Type is required.")
                return
            }
            
            guard let providerOrgCode = jsonObject["providerOrgCode"] as? String, !endpoint.isEmpty else {
                sendError(message: "Provider Org Code is required", callbackId: command.callbackId)
                os_log("Provider Org Code is required.")
                return
            }
            
            guard let tokenAPI = jsonObject["tokenApi"] as? String, !endpoint.isEmpty else {
                sendError(message: "Token API is required", callbackId: command.callbackId)
                os_log("Token API is required.")
                return
            }
            
            os_log(
                 """
                 Config Name: %{public}@, Endpoint URL: %{public}@, Client Id: %{public}@, Key Id: %{public}@, \
                 Client Security: %{private}@, Grant Type: %{public}@, Provider Org Code: %{public}@, Token API: %{public}@
                 """,
                 log: OSLog.default, type: .info,
                 config, endpoint, clientId, keyId, clientSecret, grantType, providerOrgCode, tokenAPI
             )
                      

            if sdk == nil {
                initializeSDK(apiKey: apiKey)
            }

            guard let sdk = sdk else {
                sendError(message: "SDK initialization failed.", callbackId: command.callbackId)
                return
            }

            customTestHandler = CustomTestHandler(speedTestsdk: sdk, configName: config, count: count, timeInterval: timeInterval) { result in
                let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: result)
                print(result)
                self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
            }

            customTestHandler?.runTestWithSingleServer(endpoint: endpoint, clientId: clientId, keyId: keyId, clientSecret: clientSecret, grantType: grantType, providerOrgCode: providerOrgCode, tokenAPI: tokenAPI)


        } catch {
            sendError(message: "Error parsing JSON: \(error.localizedDescription)", callbackId: command.callbackId)
        }
    }


}
