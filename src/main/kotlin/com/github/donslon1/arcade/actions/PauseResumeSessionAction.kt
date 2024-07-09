package com.github.donslon1.arcade.actions

import com.github.donslon1.arcade.services.ApiService
import com.github.donslon1.arcade.services.ApiSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class PauseResumeSessionAction : AnAction("Pause/Resume Hack Hour Session") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val apiSettings = ApiSettings.getInstance(project)
        val apiService = ApiService(apiSettings.apiKey)

        val session = apiService.pauseResumeSession(apiSettings.slackId)
        if (session != null) {
            val status = if (session.paused) "paused" else "resumed"
            Messages.showInfoMessage("Session $status successfully!", "Hack Hour")
        } else {
            Messages.showErrorDialog("Failed to pause/resume session", "Hack Hour")
        }
    }
}
