import Foundation

struct ResultDTO: Codable {
    let id: String
    let result: String

    func toDictionary() -> [String: Any] {
        return [
            "loop": id,
            "result": result
        ]
    }
    func toJson() -> [String: Any] {
        return toDictionary()
    }
}

func convertTestResultsToJson(testResults: [ResultDTO]) -> String {
    let jsonArray = testResults.map { $0.toJson() }
    if let jsonData = try? JSONSerialization.data(withJSONObject: jsonArray, options: .prettyPrinted),
       let jsonString = String(data: jsonData, encoding: .utf8) {
        return jsonString
    }
    return "[]"
}

func convertTestResultsToJsonArray(testResults: [ResultDTO]) -> [String: Any] {
    var resultDictionary: [String: Any] = [:]

    for result in testResults {
        var jsonObject = result.toDictionary()
        if let resultData = result.result.data(using: .utf8),
           let resultJsonObject = try? JSONSerialization.jsonObject(with: resultData, options: []),
           let resultDictionaryItem = resultJsonObject as? [String: Any] {
            jsonObject["result"] = resultDictionaryItem
        }

        resultDictionary[result.id] = jsonObject
    }

    return resultDictionary
}
