import os

protocol APIServiceInterface {
    func sendResult(url: String, payload: String, headers: [String: String]?)
    func getAuthToken(
        url: String,
        payload: [String: String],
        headers: [String: String],
        callback: @escaping (String?) -> Void
    )
    
}

class APIService: APIServiceInterface{
    
    func sendResult(url: String, payload: String, headers: [String: String]?) {
   
        guard let url = URL(string: url) else {
            os_log("Invalid URL.")
            return
        }
      
        guard
            let token = headers?["token"],
            let providerOrgCode = headers?["providerOrgCode"],
            let transactionId = headers?["transactionId"],
            let keyId = headers?["keyId"]
        else {
            os_log("Missing required headers: token, providerOrgCode, transactionId, or keyId.")
            return
        }
        
        let currentEpochTimeInMilliseconds = Int(Date().timeIntervalSince1970 * 1000)
        let timestamp = headers?["timestamp"] ?? String(currentEpochTimeInMilliseconds)
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue(token, forHTTPHeaderField: "token")
        request.setValue(providerOrgCode, forHTTPHeaderField: "providerOrgCode")
        request.setValue(transactionId, forHTTPHeaderField: "transactionId")
        request.setValue(keyId, forHTTPHeaderField: "keyId")
        request.setValue(timestamp, forHTTPHeaderField: "timestamp")
        request.httpBody = payload.data(using: .utf8)
        
        os_log("Request headers: %@", request.allHTTPHeaderFields ?? [:])
        if let httpBody = request.httpBody, let bodyString = String(data: httpBody, encoding: .utf8) {
            os_log("Request body: %@", bodyString)
        } else {
            os_log("Request body is nil or could not be converted to a string")
        }
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                os_log("Error: %@", error.localizedDescription)
                return
            }
            
            if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    os_log("Data sent successfully.")
                    
                    if let data = data, let responseString = String(data: data, encoding: .utf8) {
                        os_log("Response: %@", responseString)
                    } else {
                        os_log("No response data received.")
                    }
                } else {
                    os_log("Failed to send data. HTTP Status Code: %d", httpResponse.statusCode)
                }
            } else {
                os_log("Invalid HTTP response.")
            }
        }
        
        task.resume()
    }

    
    func getAuthToken(url: String, payload: [String: String], headers: [String: String], callback: @escaping (String?) -> Void) {
        guard let requestURL = URL(string: url) else {
            os_log("Invalid URL.")
            return
        }
        guard let keyId = headers["keyId"], !keyId.isEmpty else {
            os_log("Missing or empty keyId in headers.")
            return
        }
        
        var request = URLRequest(url: requestURL)
        request.httpMethod = "POST"
        
        let formBody = payload.map { "\($0.key)=\($0.value)" }.joined(separator: "&")
        request.httpBody = formBody.data(using: .utf8)

        request.setValue(keyId, forHTTPHeaderField: "KeyId")
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
     
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                os_log("Error: %{public}@", error.localizedDescription)
                return
            }
           
            guard let httpResponse = response as? HTTPURLResponse else {
                os_log("Failed to get HTTP response.")
                return
            }
            
        
            guard httpResponse.statusCode == 200 else {
                os_log("Server returned status code: %d", httpResponse.statusCode)
                return
            }
            
        
            guard let data = data else {
                os_log("No data received in response.")
                return
            }
      
            do {
                if let jsonObject = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any],
                   let accessToken = jsonObject["access_token"] as? String {
                    os_log("Token received successfully.")
                    callback(accessToken)
                }
            } catch {
                os_log("JSON Parsing Error: %{public}@", error.localizedDescription)
            }
        }
        
        task.resume()
    }

    
}
