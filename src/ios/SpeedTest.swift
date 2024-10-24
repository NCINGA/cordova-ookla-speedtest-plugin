import Foundation
import Cordova

@objc(SpeedTest) class SpeedTest: CDVPlugin {

    @objc(startTesting:)
    func startTesting(command: CDVInvokedUrlCommand) {
        let message = command.arguments[0] as? String ?? ""
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: message)
        self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
    }
}
