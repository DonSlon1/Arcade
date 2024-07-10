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

    fun getSession(slackId: String): Session? {
        val (_, _, result) = Fuel.get("$baseUrl/api/session/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, SessionResponse::class.java).data
            else -> {
                logger.error("Failed to get session")
                null
            }
        }
    }

    fun getStats(slackId: String): Stats? {
        val (_, _, result) = Fuel.get("$baseUrl/api/stats/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, StatsResponse::class.java).data
            else -> {
                logger.error("Failed to get stats")
                null
            }
        }
    }

    fun getGoals(slackId: String): List<Goal>? {
        val (_, _, result) = Fuel.get("$baseUrl/api/goals/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> gson.fromJson(result.value, GoalsResponse::class.java).data.goals
            else -> {
                logger.error("Failed to get goals")
                null
            }
        }
    }

    fun stopSession(slackId: String): Session? {
        val (_, _, result) = Fuel.post("$baseUrl/api/stop/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> {
                val stoppedSession = gson.fromJson(result.value, SessionResponse::class.java).data
                currentSession = null
                stoppedSession
            }
            else -> {
                logger.error("Failed to stop session")
                null
            }
        }
    }

    fun getSessions(slackId: String): List<Session> {
        try {
            val (_, _, result) = Fuel.get("$baseUrl/api/history/$slackId")
                .header(getHeaders())
                .responseString()

            return when (result) {
                is Result.Success -> {
                    gson.fromJson(result.value, SessionListResponse::class.java).data
                }
                is Result.Failure -> {
                    logger.warn("Failed to fetch sessions: ${result.error}")
                    emptyList()
                }
            }
        } catch (e: Exception) {
            logger.error("Error fetching sessions", e)
            return emptyList()
        }
    }

    private var currentSession: Session? = null

    fun getCurrentSession(): Session? = currentSession

    fun startSession(slackId: String, work: String): Session? {
        val (_, _, result) = Fuel.post("$baseUrl/api/start/$slackId")
            .header(getHeaders())
            .jsonBody("""{"work": "$work"}""")
            .responseString()

        return when (result) {
            is Result.Success -> {
                currentSession = gson.fromJson(result.value, SessionResponse::class.java).data
                currentSession
            }
            else -> {
                logger.error("Failed to start session")
                null
            }
        }
    }

    fun pauseResumeSession(slackId: String): Session? {
        val (_, _, result) = Fuel.post("$baseUrl/api/pause/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> {
                currentSession = gson.fromJson(result.value, SessionResponse::class.java).data
                currentSession
            }
            else -> {
                logger.error("Failed to pause/resume session")
                null
            }
        }
    }

    fun cancelSession(slackId: String): Session? {
        val (_, _, result) = Fuel.post("$baseUrl/api/cancel/$slackId")
            .header(getHeaders())
            .responseString()

        return when (result) {
            is Result.Success -> {
                val cancelledSession = gson.fromJson(result.value, SessionResponse::class.java).data
                currentSession = null
                cancelledSession
            }
            else -> {
                logger.error("Failed to cancel session")
                null
            }
        }
    }

    fun hasActiveSession(slackId: String): Boolean {
        if (currentSession != null && !currentSession!!.completed) {
            return true
        }
        val session = getSession(slackId)
        currentSession = session
        return session != null && !session.completed
    }
}

// Data classes for API responses
data class SessionResponse(val ok: Boolean, val data: Session)
data class StatsResponse(val ok: Boolean, val data: Stats)
data class GoalsResponse(val ok: Boolean, val data: GoalsData)
data class SessionListResponse(val ok: Boolean, val data: List<Session>)

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
    val messageTs: String,
    val work: String
)

data class Stats(val sessions: Int, val total: Int)
data class GoalsData(val goals: List<Goal>)
data class Goal(val name: String, val minutes: Int)
