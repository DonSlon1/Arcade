package com.github.donslon1.arcade.actions

import com.github.donslon1.arcade.services.ApiService
import com.github.donslon1.arcade.services.ApiSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class StartSessionAction : AnAction("Start Hack Hour Session") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val apiSettings = ApiSettings.getInstance(project)
        val apiService = ApiService(apiSettings.apiKey)

        val work = Messages.showInputDialog(project, "What are you working on?", "Start Session", null)
        if (work != null) {
            val session = apiService.startSession(apiSettings.slackId, work)
            if (session != null) {
                Messages.showInfoMessage("Session started successfully!", "Hack Hour")
            } else {
                Messages.showErrorDialog("Failed to start session", "Hack Hour")
            }
        }
    }
}
