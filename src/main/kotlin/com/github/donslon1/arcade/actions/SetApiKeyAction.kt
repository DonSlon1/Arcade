package com.github.donslon1.arcade.actions

import com.github.donslon1.arcade.services.ApiSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class SetApiKeyAction : AnAction("Set API Key and Slack ID") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val apiSettings = ApiSettings.getInstance(project)
        val dialog = ApiKeyDialog(apiSettings.apiKey, apiSettings.slackId)
        if (dialog.showAndGet()) {
            apiSettings.apiKey = dialog.getApiKey()
            apiSettings.slackId = dialog.getSlackId()
        }
    }
}

class ApiKeyDialog(initialApiKey: String, initialSlackId: String) : DialogWrapper(true) {
    private val apiKeyField = JBTextField(initialApiKey)
    private val slackIdField = JBTextField(initialSlackId)

    init {
        title = "Set API Key and Slack ID"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row("API Key:") {
                cell(apiKeyField)
                    .focused()
                    .resizableColumn()
            }
            row("Slack ID:") {
                cell(slackIdField)
                    .resizableColumn()
            }
        }
    }

    fun getApiKey(): String = apiKeyField.text
    fun getSlackId(): String = slackIdField.text
}
