package com.github.donslon1.arcade.services

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.intellij.openapi.diagnostic.Logger

class ApiService(private val apiKey: String) {
    private val baseUrl = "https://hackhour.hackclub.com" // Replace with actual base URL
    private val gson = Gson()
    private val logger = Logger.getInstance(ApiService::class.java)

    private fun getHeaders() = mapOf("Authorization" to "Bearer $apiKey")

    private fun <T> logResponse(endpoint: String, result: Result<String, Exception>, responseClass: Class<T>) {
        when (result) {
            is Result.Success -> {
                logger.info("Successful API call to $endpoint")
                logger.debug("Response: ${result.value}")
                try {
                    val response = gson.fromJson(result.value, responseClass)
                    logger.debug("Parsed response: $response")
                } catch (e: Exception) {
                    logger.error("Failed to parse response from $endpoint", e)
                }
            }
            is Result.Failure -> {
                logger.error("API call to $endpoint failed", result.error)
            }
        }
    }

    fun getSession(slackId: String): Session? {
        val endpoint = "$baseUrl/api/session/$slackId"
        val (_, _, result) = Fuel.get(endpoint)
            .header(getHeaders())
            .responseString()

        logResponse(endpoint, result, SessionResponse::class.java)

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, SessionResponse::class.java).data
            else -> null
        }
    }

    fun getStats(slackId: String): Stats? {
        val (_, _, result) = Fuel.get("$baseUrl/api/stats/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, StatsResponse::class.java).data
            else -> null
        }
    }

    fun getGoals(slackId: String): List<Goal>? {
        val (_, _, result) = Fuel.get("$baseUrl/api/goals/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, GoalsResponse::class.java).data.goals
            else -> null
        }
    }

    fun getHistory(slackId: String): List<HistoryItem>? {
        val (_, _, result) = Fuel.get("$baseUrl/api/history/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, HistoryResponse::class.java).data
            else -> null
        }
    }

    fun startSession(slackId: String, work: String): Session? {
        val endpoint = "$baseUrl/api/start/$slackId"
        logger.info("Starting session for work: $work")
        logger.info("API endpoint: $endpoint")
        val (_, _, result) = Fuel.post(endpoint)
            .header(getHeaders())
            .jsonBody("""{"work": "$work"}""")
            .responseString()

        logResponse(endpoint, result, SessionResponse::class.java)

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, SessionResponse::class.java).data
            else -> null
        }
    }

    fun pauseResumeSession(slackId: String): Session? {
        val (_, _, result) = Fuel.post("$baseUrl/api/pause/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, SessionResponse::class.java).data
            else -> null
        }
    }

    fun cancelSession(slackId: String): Session? {
        val (_, _, result) = Fuel.post("$baseUrl/api/cancel/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, SessionResponse::class.java).data
            else -> null
        }
    }
}

// Data classes for API responses
data class SessionResponse(val ok: Boolean, val data: Session)
data class StatsResponse(val ok: Boolean, val data: Stats)
data class GoalsResponse(val ok: Boolean, val data: GoalsData)
data class HistoryResponse(val ok: Boolean, val data: List<HistoryItem>)

data class Session(
    val id: String,
    val createdAt: String,
    val time: Int,
    val elapsed: Int,
    val remaining: Int,
    val endTime: String,
    val goal: String,
    val paused: Boolean,
    val completed: Boolean,
    val messageTs: String
)

data class Stats(val sessions: Int, val total: Int)
data class GoalsData(val goals: List<Goal>)
data class Goal(val name: String, val minutes: Int)
data class HistoryItem(
    val createdAt: String,
    val time: Int,
    val elapsed: Int,
    val goal: String,
    val ended: Boolean,
    val work: String
)
