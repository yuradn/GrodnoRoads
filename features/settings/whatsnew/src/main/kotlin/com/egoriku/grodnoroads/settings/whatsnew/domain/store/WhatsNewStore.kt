package com.egoriku.grodnoroads.settings.whatsnew.domain.store

import com.arkivanov.mvikotlin.core.store.Store
import com.egoriku.grodnoroads.settings.whatsnew.domain.model.ReleaseNotes
import com.egoriku.grodnoroads.settings.whatsnew.domain.store.WhatsNewStore.State

interface WhatsNewStore : Store<Nothing, State, Nothing> {

    sealed class Message {
        data class Loading(val isLoading: Boolean) : Message()
        data class Success(val releaseNotes: List<ReleaseNotes>) : Message()
    }

    data class State(
        val isLoading: Boolean = false,
        val releaseNotes: List<ReleaseNotes> = emptyList()
    )
}