package com.egoriku.grodnoroads.screen.main

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.egoriku.grodnoroads.map.domain.component.MapComponent
import com.egoriku.grodnoroads.settings.root.domain.component.SettingsComponent

interface MainComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Child(val index: Int) {
        data class Map(val component: MapComponent) : Child(index = 0)
        data class Settings(val component: SettingsComponent) : Child(index = 1)
    }

    fun onSelectTab(index: Int)
}