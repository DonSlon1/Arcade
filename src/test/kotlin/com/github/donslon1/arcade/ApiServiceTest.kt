package com.github.donslon1.arcade

import com.github.donslon1.arcade.services.ApiService
import com.github.donslon1.arcade.services.Session
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.junit.jupiter.api.Assertions.*

class ApiServiceTest {

    private lateinit var apiService: ApiService
    private val mockApiKey = "test_api_key"
    private val mockSlackId = "test_slack_id"

    @BeforeEach
    fun setup() {
        apiService = mock(ApiService::class.java)
    }

    @Test
    fun testStartSession() {
        val mockSession = Session("1", "2023-07-10T10:00:00Z", 60, 0, 60, "2023-07-10T11:00:00Z", "Test Goal", false, false, "123", "Test Work")
        `when`(apiService.startSession(mockSlackId, "Test Work")).thenReturn(mockSession)

        val result = apiService.startSession(mockSlackId, "Test Work")
        assertNotNull(result)
        assertEquals("Test Work", result?.work)
    }

    @Test
    fun testPauseResumeSession() {
        val mockSession = Session("1", "2023-07-10T10:00:00Z", 60, 30, 30, "2023-07-10T11:00:00Z", "Test Goal", true, false, "123", "Test Work")
        `when`(apiService.pauseResumeSession(mockSlackId)).thenReturn(mockSession)

        val result = apiService.pauseResumeSession(mockSlackId)
        assertNotNull(result)
        assertTrue(result?.paused ?: false)
    }

    @Test
    fun testStopSession() {
        val mockSession = Session("1", "2023-07-10T10:00:00Z", 60, 60, 0, "2023-07-10T11:00:00Z", "Test Goal", false, true, "123", "Test Work")
        `when`(apiService.stopSession(mockSlackId)).thenReturn(mockSession)

        val result = apiService.stopSession(mockSlackId)
        assertNotNull(result)
        assertTrue(result?.completed ?: false)
    }

    @Test
    fun testCancelSession() {
        val mockSession = Session("1", "2023-07-10T10:00:00Z", 60, 30, 30, "2023-07-10T11:00:00Z", "Test Goal", false, true, "123", "Test Work")
        `when`(apiService.cancelSession(mockSlackId)).thenReturn(mockSession)

        val result = apiService.cancelSession(mockSlackId)
        assertNotNull(result)
        assertTrue(result?.completed ?: false)
    }

    @Test
    fun testGetSessions() {
        val mockSessions = listOf(
            Session("1", "2023-07-10T10:00:00Z", 60, 60, 0, "2023-07-10T11:00:00Z", "Test Goal 1", false, true, "123", "Test Work 1"),
            Session("2", "2023-07-10T12:00:00Z", 30, 30, 0, "2023-07-10T12:30:00Z", "Test Goal 2", false, true, "124", "Test Work 2")
        )
        `when`(apiService.getSessions(mockSlackId)).thenReturn(mockSessions)

        val result = apiService.getSessions(mockSlackId)
        assertNotNull(result)
        assertEquals(2, result?.size)
    }
}
