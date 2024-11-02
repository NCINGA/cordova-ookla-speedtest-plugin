package com.ncinga.speedtest

import org.json.JSONArray
import org.json.JSONObject

data class ResultDTO(val id: String, val result: String) {
    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("loop", id)
        jsonObject.put("result", result)
        return jsonObject
    }
}

fun convertTestResultsToJson(testResults: MutableList<ResultDTO>): String {
    val jsonArray = JSONArray()
    for (result in testResults) {
        jsonArray.put(result.toJson())
    }
    return jsonArray.toString(2)
}