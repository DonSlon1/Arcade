package com.github.donslon1.arcade.actions

import com.github.donslon1.arcade.services.ApiService
import com.github.donslon1.arcade.services.ApiSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class CancelSessionAction : AnAction("Cancel Hack Hour Session") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val apiSettings = ApiSettings.getInstance(project)
        val apiService = ApiService(apiSettings.apiKey)

        val session = apiService.cancelSession(apiSettings.slackId)
        if (session != null) {
            Messages.showInfoMessage("Session canceled successfully!", "Hack Hour")
        } else {
            Messages.showErrorDialog("Failed to cancel session", "Hack Hour")
        }
    }
}
