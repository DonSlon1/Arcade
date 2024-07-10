package com.github.donslon1.arcade.toolWindow

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.github.donslon1.arcade.services.ApiService
import com.github.donslon1.arcade.services.ApiSettings
import com.github.donslon1.arcade.services.Session
import javax.swing.*
import java.awt.BorderLayout
import java.awt.Dimension

class HackHourPanel(private val project: Project) : JPanel(BorderLayout()) {
    private val startButton = JButton("Start Session")
    private val pauseResumeButton = JButton("Pause/Resume")
    private val stopButton = JButton("Stop Session")
    private val cancelButton = JButton("Cancel Session")
    private val workField = JTextField(20)
    private val sessionList = JList<String>()
    private val currentSessionLabel = JLabel()
    private val logger = Logger.getInstance(HackHourPanel::class.java)
    private var apiService: ApiService? = null
    private var slackId: String = ""
    private var updateTimer: Timer? = null

    init {
        val apiSettings = ApiSettings.getInstance(project)
        apiService = ApiService(apiSettings.apiKey)
        slackId = apiSettings.slackId

        val controlPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("What are you working on?"))
            add(workField)
            add(createButtonPanel())
        }

        add(controlPanel, BorderLayout.NORTH)
        add(JScrollPane(sessionList), BorderLayout.CENTER)
        add(currentSessionLabel, BorderLayout.SOUTH)

        startButton.addActionListener { startSession() }
        pauseResumeButton.addActionListener { pauseResumeSession() }
        stopButton.addActionListener { stopSession() }
        cancelButton.addActionListener { cancelSession() }

        updateSessionList()
        updateButtonStates()
        startUpdateTimer()
    }

    private fun createButtonPanel(): JPanel {
        return JPanel().apply {
            add(startButton)
            add(pauseResumeButton)
            add(stopButton)
            add(cancelButton)
        }
    }

    private fun updateButtonStates() {
        val hasActiveSession = apiService?.hasActiveSession(slackId) ?: false
        startButton.isEnabled = !hasActiveSession
        pauseResumeButton.isEnabled = hasActiveSession
        stopButton.isEnabled = hasActiveSession
        cancelButton.isEnabled = hasActiveSession
        workField.isEnabled = !hasActiveSession
    }

    private fun startSession() {
        val work = workField.text
        if (work.isBlank()) {
            Messages.showWarningDialog(project, "Please enter what you're working on", "Hack Hour")
            return
        }

        apiService?.startSession(slackId, work)?.let {
            Messages.showInfoMessage(project, "Session started successfully!", "Hack Hour")
            updateSessionList()
            updateButtonStates()
            updateCurrentSessionInfo()
        } ?: Messages.showErrorDialog(project, "Failed to start session", "Hack Hour")
    }

    private fun pauseResumeSession() {
        apiService?.pauseResumeSession(slackId)?.let {
            val status = if (it.paused) "paused" else "resumed"
            Messages.showInfoMessage(project, "Session $status successfully!", "Hack Hour")
            updateButtonStates()
            updateCurrentSessionInfo()
        } ?: Messages.showErrorDialog(project, "Failed to pause/resume session", "Hack Hour")
    }

    private fun stopSession() {
        apiService?.stopSession(slackId)?.let {
            Messages.showInfoMessage(project, "Session stopped successfully!", "Hack Hour")
            updateSessionList()
            updateButtonStates()
            updateCurrentSessionInfo()
        } ?: Messages.showErrorDialog(project, "Failed to stop session", "Hack Hour")
    }

    private fun cancelSession() {
        val result = Messages.showYesNoDialog(
            project,
            "Are you sure you want to cancel the current session? This action cannot be undone.",
            "Cancel Session",
            Messages.getQuestionIcon()
        )
        if (result == Messages.YES) {
            apiService?.cancelSession(slackId)?.let {
                Messages.showInfoMessage(project, "Session canceled successfully!", "Hack Hour")
                updateSessionList()
                updateButtonStates()
                updateCurrentSessionInfo()
            } ?: Messages.showErrorDialog(project, "Failed to cancel session", "Hack Hour")
        }
    }

    private fun updateSessionList() {
        val sessions = apiService?.getSessions(slackId) ?: emptyList()
        val sessionStrings = sessions.map { "${it.work} (${formatDuration(it.elapsed)})" }
        sessionList.setListData(sessionStrings.toTypedArray())
    }

    private fun updateCurrentSessionInfo() {
        val currentSession = apiService?.getCurrentSession()
        if (currentSession != null) {
            val elapsedTime = formatDuration(currentSession.elapsed)
            val status = if (currentSession.paused) "Paused" else "Running"
            currentSessionLabel.text = "Current Session: ${currentSession.work} - $elapsedTime - $status"
        } else {
            currentSessionLabel.text = "No active session"
        }
    }

    private fun startUpdateTimer() {
        updateTimer = Timer(5000) { // Update every 5 seconds
            SwingUtilities.invokeLater {
                updateCurrentSessionInfo()
                updateButtonStates()
            }
        }
        updateTimer?.start()
    }

    private fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "$hours h $mins min" else "$mins min"
    }

    override fun removeNotify() {
        super.removeNotify()
        updateTimer?.stop()
    }
}
