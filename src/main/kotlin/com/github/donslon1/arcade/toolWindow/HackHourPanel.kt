package com.github.donslon1.arcade.toolWindow

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.github.donslon1.arcade.services.ApiService
import com.github.donslon1.arcade.services.ApiSettings
import javax.swing.*

class HackHourPanel(private val project: Project) : JPanel() {
    private val startButton = JButton("Start Session")
    private val pauseResumeButton = JButton("Pause/Resume")
    private val cancelButton = JButton("Cancel Session")
    private val workField = JTextField(20)
    private val logger = Logger.getInstance(HackHourPanel::class.java)

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        add(JLabel("What are you working on?"))
        add(workField)
        add(startButton)
        add(pauseResumeButton)
        add(cancelButton)

        startButton.addActionListener { startSession() }
        pauseResumeButton.addActionListener { pauseResumeSession() }
        cancelButton.addActionListener { cancelSession() }
    }

    private fun getApiServiceAndSlackId(): Pair<ApiService?, String> {
        val apiSettings = ApiSettings.getInstance(project)
        val apiKey = apiSettings.apiKey
        val slackId = apiSettings.slackId

        if (apiKey.isBlank()) {
            logger.warn("API key is not set")
            Messages.showErrorDialog(project, "Please set your API key in the settings.", "API Key Missing")
            return null to ""
        }

        if (slackId.isBlank()) {
            logger.warn("Slack ID is not set")
            Messages.showErrorDialog(project, "Please set your Slack ID in the settings.", "Slack ID Missing")
            return null to ""
        }

        return ApiService(apiKey) to slackId
    }

    private fun startSession() {
        val (apiService, slackId) = getApiServiceAndSlackId()
        if (apiService == null) return

        val work = workField.text

        if (work.isBlank()) {
            logger.warn("Attempted to start session with blank work description")
            Messages.showWarningDialog(project, "Please enter what you're working on", "Hack Hour")
            return
        }

        logger.info("Starting session for work: $work")
        val session = apiService.startSession(slackId, work)
        if (session != null) {
            logger.info("Session started successfully: $session")
            Messages.showInfoMessage(project, "Session started successfully!", "Hack Hour")
        } else {
            logger.error("Failed to start session")
            Messages.showErrorDialog(project, "Failed to start session", "Hack Hour")
        }
    }

    private fun pauseResumeSession() {
        val (apiService, slackId) = getApiServiceAndSlackId()
        if (apiService == null) return

        logger.info("Attempting to pause/resume session")
        val session = apiService.pauseResumeSession(slackId)
        if (session != null) {
            val status = if (session.paused) "paused" else "resumed"
            logger.info("Session $status successfully: $session")
            Messages.showInfoMessage(project, "Session $status successfully!", "Hack Hour")
        } else {
            logger.error("Failed to pause/resume session")
            Messages.showErrorDialog(project, "Failed to pause/resume session", "Hack Hour")
        }
    }

    private fun cancelSession() {
        val (apiService, slackId) = getApiServiceAndSlackId()
        if (apiService == null) return

        logger.info("Attempting to cancel session")
        val session = apiService.cancelSession(slackId)
        if (session != null) {
            logger.info("Session canceled successfully: $session")
            Messages.showInfoMessage(project, "Session canceled successfully!", "Hack Hour")
        } else {
            logger.error("Failed to cancel session")
            Messages.showErrorDialog(project, "Failed to cancel session", "Hack Hour")
        }
    }
}
