package com.github.donslon1.arcade.startup

import com.github.donslon1.arcade.dialogs.ApiKeySettingsDialog
import com.github.donslon1.arcade.services.ApiSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class ApiKeyStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        val apiSettings = ApiSettings.getInstance(project)

        if (apiSettings.apiKey.isBlank()) {
            val dialog = ApiKeySettingsDialog()
            if (dialog.showAndGet()) {
                val apiKey = dialog.getApiKey()
                apiSettings.apiKey = apiKey
            }
        }
    }
}
