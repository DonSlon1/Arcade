package com.github.donslon1.arcade.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class HackHourToolWindow : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(HackHourPanel(project), "", false)
        toolWindow.contentManager.addContent(content)
    }
}
