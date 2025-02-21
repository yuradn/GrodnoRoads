package com.egoriku.grodnoroads.settings.whatsnew.domain.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.egoriku.grodnoroads.settings.whatsnew.domain.store.WhatsNewStore
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal class WhatsNewComponentImpl(
    componentContext: ComponentContext
) : WhatsNewComponent, KoinComponent, ComponentContext by componentContext {

    private val whatsNewStore: WhatsNewStore = instanceKeeper.getStore(::get)

    override val state: Flow<WhatsNewStore.State>
        get() = whatsNewStore.states
}