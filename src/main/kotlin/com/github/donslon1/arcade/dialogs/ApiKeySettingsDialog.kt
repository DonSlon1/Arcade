package com.github.donslon1.arcade.dialogs

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class ApiKeySettingsDialog(initialApiKey: String = "", initialSlackId: String = "") : DialogWrapper(true) {
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
