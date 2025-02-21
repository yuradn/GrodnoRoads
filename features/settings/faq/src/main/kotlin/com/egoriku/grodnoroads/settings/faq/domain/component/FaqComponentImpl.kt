package com.egoriku.grodnoroads.settings.faq.domain.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.egoriku.grodnoroads.settings.faq.domain.store.FaqStore
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal class FaqComponentImpl(
    componentContext: ComponentContext
) : FaqComponent, KoinComponent, ComponentContext by componentContext {

    private val faqStore: FaqStore = instanceKeeper.getStore(::get)

    override val state: Flow<FaqStore.State>
        get() = faqStore.states
}