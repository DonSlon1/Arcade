package com.github.donslon1.arcade.startup

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.github.donslon1.arcade.dialogs.ApiKeySettingsDialog
import com.github.donslon1.arcade.services.ApiSettings
import com.intellij.openapi.application.invokeLater

class ApiKeyStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val apiSettings = ApiSettings.getInstance(project)

        if (apiSettings.apiKey.isBlank() || apiSettings.slackId.isBlank()) {
            invokeLater {
                val dialog = ApiKeySettingsDialog(apiSettings.apiKey, apiSettings.slackId)
                if (dialog.showAndGet()) {
                    apiSettings.apiKey = dialog.getApiKey()
                    apiSettings.slackId = dialog.getSlackId()
                }
            }
        }
    }
}
