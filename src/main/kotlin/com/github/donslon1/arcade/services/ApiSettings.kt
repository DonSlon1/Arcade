package com.github.donslon1.arcade.services

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "ArcadeApiSettings",
    storages = [Storage("arcadeApiSettings.xml")]
)
class ApiSettings : PersistentStateComponent<ApiSettings.State> {
    data class State(var apiKey: String = "", var slackId: String = "")

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    var apiKey: String
        get() = myState.apiKey
        set(value) {
            myState.apiKey = value
        }

    var slackId: String
        get() = myState.slackId
        set(value) {
            myState.slackId = value
        }

    companion object {
        fun getInstance(project: Project): ApiSettings =
            project.getService(ApiSettings::class.java)
    }
}
