package com.github.donslon1.arcade

import com.github.donslon1.arcade.services.ApiSettings
import com.intellij.openapi.project.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.junit.jupiter.api.Assertions.*

class ApiSettingsTest {

    private lateinit var apiSettings: ApiSettings
    private lateinit var mockProject: Project

    @BeforeEach
    fun setup() {
        mockProject = mock(Project::class.java)
        apiSettings = ApiSettings()
    }

    @Test
    fun testGetSetApiKey() {
        apiSettings.apiKey = "test_api_key"
        assertEquals("test_api_key", apiSettings.apiKey)
    }

    @Test
    fun testGetSetSlackId() {
        apiSettings.slackId = "test_slack_id"
        assertEquals("test_slack_id", apiSettings.slackId)
    }

    @Test
    fun testGetInstance() {
        val mockApiSettings = mock(ApiSettings::class.java)
        `when`(mockProject.getService(ApiSettings::class.java)).thenReturn(mockApiSettings)

        val result = ApiSettings.getInstance(mockProject)
        assertNotNull(result)
        assertEquals(mockApiSettings, result)
    }
}
