package com.github.donslon1.arcade

import com.github.donslon1.arcade.services.ApiService
import com.github.donslon1.arcade.services.ApiSettings
import com.github.donslon1.arcade.toolWindow.HackHourPanel
import com.intellij.openapi.project.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import javax.swing.JButton
import javax.swing.JTextField
import org.junit.jupiter.api.Assertions.*

class HackHourPanelTest {

    private lateinit var hackHourPanel: HackHourPanel
    private lateinit var mockProject: Project
    private lateinit var mockApiService: ApiService
    private lateinit var mockApiSettings: ApiSettings

    @BeforeEach
    fun setup() {
        mockProject = mock(Project::class.java)
        mockApiService = mock(ApiService::class.java)
        mockApiSettings = mock(ApiSettings::class.java)

        `when`(mockApiSettings.apiKey).thenReturn("test_api_key")
        `when`(mockApiSettings.slackId).thenReturn("test_slack_id")
        `when`(mockProject.getService(ApiSettings::class.java)).thenReturn(mockApiSettings)

        hackHourPanel = HackHourPanel(mockProject)
        // Inject mocked ApiService
        val apiServiceField = HackHourPanel::class.java.getDeclaredField("apiService")
        apiServiceField.isAccessible = true
        apiServiceField.set(hackHourPanel, mockApiService)
    }

    @Test
    fun testStartSession() {
        val workField = hackHourPanel.javaClass.getDeclaredField("workField")
        workField.isAccessible = true
        (workField.get(hackHourPanel) as JTextField).text = "Test Work"

        val startButton = hackHourPanel.javaClass.getDeclaredField("startButton")
        startButton.isAccessible = true
        (startButton.get(hackHourPanel) as JButton).doClick()

        verify(mockApiService).startSession("test_slack_id", "Test Work")
    }

    @Test
    fun testPauseResumeSession() {
        val pauseResumeButton = hackHourPanel.javaClass.getDeclaredField("pauseResumeButton")
        pauseResumeButton.isAccessible = true
        (pauseResumeButton.get(hackHourPanel) as JButton).doClick()

        verify(mockApiService).pauseResumeSession("test_slack_id")
    }

    @Test
    fun testStopSession() {
        val stopButton = hackHourPanel.javaClass.getDeclaredField("stopButton")
        stopButton.isAccessible = true
        (stopButton.get(hackHourPanel) as JButton).doClick()

        verify(mockApiService).stopSession("test_slack_id")
    }

    @Test
    fun testCancelSession() {
        val cancelButton = hackHourPanel.javaClass.getDeclaredField("cancelButton")
        cancelButton.isAccessible = true
        (cancelButton.get(hackHourPanel) as JButton).doClick()

        verify(mockApiService).cancelSession("test_slack_id")
    }
}
