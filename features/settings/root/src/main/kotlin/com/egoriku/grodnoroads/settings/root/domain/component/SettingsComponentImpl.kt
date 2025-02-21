package com.egoriku.grodnoroads.settings.root.domain.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.egoriku.grodnoroads.settings.root.domain.component.SettingsComponent.Child
import com.egoriku.grodnoroads.settings.root.domain.component.SettingsComponent.Child.*
import com.egoriku.grodnoroads.settings.root.domain.component.SettingsComponent.Child.Map
import com.egoriku.grodnoroads.settings.root.domain.model.Page
import com.egoriku.grodnoroads.shared.appcomponent.AppBuildConfig
import kotlinx.parcelize.Parcelize
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

internal class SettingsComponentImpl(
    componentContext: ComponentContext
) : SettingsComponent, KoinComponent, ComponentContext by componentContext {

    private val appBuildConfig by inject<AppBuildConfig>()

    private val navigation = StackNavigation<Config>()

    private val stack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Settings,
        handleBackButton = true,
        key = "Settings",
        childFactory = ::child
    )

    override val childStack: Value<ChildStack<*, Child>> = stack

    override val appVersion = appBuildConfig.versionName

    override fun open(page: Page) {
        when (page) {
            Page.Appearance -> navigation.push(Config.Appearance)
            Page.Map -> navigation.push(Config.Map)
            Page.Alerts -> navigation.push(Config.Alerts)
            Page.WhatsNew -> navigation.push(Config.WhatsNew)
            Page.NextFeatures -> navigation.push(Config.NextFeatures)
            Page.FAQ -> navigation.push(Config.FAQ)
        }
    }

    override fun onBack() = navigation.pop()

    private fun child(
        configuration: Config,
        componentContext: ComponentContext,
    ) = when (configuration) {
        is Config.Settings -> Settings(this)
        is Config.Appearance -> Appearance(appearanceComponent = get { parametersOf(componentContext) })
        is Config.Alerts -> Alerts(alertsComponent = get { parametersOf(componentContext) })
        is Config.Map -> Map(mapSettingsComponent = get { parametersOf(componentContext) })
        is Config.NextFeatures -> TODO()
        is Config.WhatsNew -> WhatsNew(whatsNewComponent = get { parametersOf(componentContext) })
        is Config.FAQ -> FAQ(faqComponent = get { parametersOf(componentContext) })
    }

    private sealed class Config : Parcelable {
        @Parcelize
        object Settings : Config()

        @Parcelize
        object Appearance : Config()

        @Parcelize
        object Map : Config()

        @Parcelize
        object Alerts : Config()

        @Parcelize
        object WhatsNew : Config()

        @Parcelize
        object NextFeatures : Config()

        @Parcelize
        object FAQ : Config()
    }
}