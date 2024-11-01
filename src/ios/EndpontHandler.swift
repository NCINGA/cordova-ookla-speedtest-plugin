import Foundation
import os

class EndpointHandler {
    
    private var endpoint: String
   
    init(endpoint: String) {
        self.endpoint = endpoint
    }
    
    public func sendData(body: String) {
        guard let url = URL(string: endpoint) else {
            os_log("Invalid URL.")
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        request.httpBody = body.data(using: .utf8)
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                os_log("Error: \(error.localizedDescription)")
                return
            }
            
            if let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 {
                os_log("Data sent successfully.")
                if let data = data {
                    let responseString = String(data: data, encoding: .utf8)
                    os_log("Response: \(responseString ?? "No response data")")
                }
            } else {
                os_log("Failed to send data or server error.")
            }
        }
        
        task.resume()
    }
}
