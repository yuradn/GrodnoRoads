package com.egoriku.grodnoroads.map.domain.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.egoriku.grodnoroads.map.domain.component.MapComponent.ReportDialogFlow
import com.egoriku.grodnoroads.map.domain.model.*
import com.egoriku.grodnoroads.map.domain.store.config.MapConfigStore
import com.egoriku.grodnoroads.map.domain.store.config.MapConfigStore.Intent
import com.egoriku.grodnoroads.map.domain.store.dialog.DialogStore
import com.egoriku.grodnoroads.map.domain.store.location.LocationStore
import com.egoriku.grodnoroads.map.domain.store.location.LocationStore.Label
import com.egoriku.grodnoroads.map.domain.store.mapevents.MapEventsStore
import com.egoriku.grodnoroads.map.domain.util.alertMessagesTransformation
import com.egoriku.grodnoroads.map.domain.util.filterMapEvents
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal class MapComponentImpl(
    componentContext: ComponentContext
) : MapComponent, ComponentContext by componentContext, KoinComponent {

    private val dialogStore: DialogStore = instanceKeeper.getStore(::get)
    private val locationStore: LocationStore = instanceKeeper.getStore(::get)
    private val mapConfigStore: MapConfigStore = instanceKeeper.getStore(::get)
    private val mapEventsStore: MapEventsStore = instanceKeeper.getStore(::get)

    private val mobile = mapEventsStore.states.map { it.mobileCamera }
    private val stationary = mapEventsStore.states.map { it.stationaryCameras }
    private val reports = mapEventsStore.states.map { it.reports }

    private val alertDistance = mapConfigStore.states.map { it.mapInternalConfig.alertDistance }
    private val mapInfo = mapConfigStore.states.map { it.mapInternalConfig.mapInfo }

    init {
        bind(lifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            locationStore.labels bindTo ::bindLocationLabel
        }
    }

    override val appMode: Flow<AppMode>
        get() = mapConfigStore.states.map { it.appMode }

    override val mapAlertDialog: Flow<MapAlertDialog>
        get() = dialogStore.states.map { it.mapAlertDialog }

    override val userCount: Flow<Int>
        get() = mapEventsStore.states.map { it.userCount }

    override val mapConfig: Flow<MapConfig>
        get() = mapConfigStore.states.map {
            MapConfig(
                zoomLevel = it.zoomLevel,
                googleMapStyle = it.mapInternalConfig.googleMapStyle,
                trafficJanOnMap = it.mapInternalConfig.trafficJanOnMap,
                keepScreenOn = it.mapInternalConfig.keepScreenOn
            )
        }

    override val mapEvents: Flow<ImmutableList<MapEvent>>
        get() = combine(
            flow = reports,
            flow2 = stationary,
            flow3 = mobile,
            flow4 = mapInfo,
            transform = filterMapEvents()
        ).flowOn(Dispatchers.Default)

    override val lastLocation: Flow<LastLocation>
        get() = locationStore.states.map { it.lastLocation }

    override val alerts: Flow<ImmutableList<Alert>>
        get() = combine(
            flow = mapEvents,
            flow2 = lastLocation,
            flow3 = alertDistance,
            transform = alertMessagesTransformation()
        ).flowOn(Dispatchers.Default)

    override val labels: Flow<Label> = locationStore.labels

    override fun startLocationUpdates() {
        locationStore.accept(LocationStore.Intent.StartLocationUpdates)
        mapConfigStore.accept(Intent.StartDriveMode)
    }

    override fun stopLocationUpdates() {
        locationStore.accept(LocationStore.Intent.StopLocationUpdates)
        mapConfigStore.accept(Intent.StopDriveMode)
    }

    override fun onLocationDisabled() = locationStore.accept(LocationStore.Intent.DisabledLocation)

    override fun reportAction(params: MapEventsStore.Intent.ReportAction.Params) {
        mapEventsStore.accept(MapEventsStore.Intent.ReportAction(params = params))
        dialogStore.accept(DialogStore.Intent.CloseDialog)
    }

    override fun showMarkerInfoDialog(reports: MapEvent.Reports) =
        dialogStore.accept(DialogStore.Intent.OpenMarkerInfoDialog(reports = reports))

    override fun openReportFlow(reportDialogFlow: ReportDialogFlow) {
        when (reportDialogFlow) {
            is ReportDialogFlow.TrafficPolice -> dialogStore.accept(
                intent = DialogStore.Intent.OpenReportTrafficPoliceDialog(reportDialogFlow.latLng)
            )

            is ReportDialogFlow.RoadIncident -> dialogStore.accept(
                intent = DialogStore.Intent.OpenRoadIncidentDialog(reportDialogFlow.latLng)
            )
        }
    }

    override fun closeDialog() = dialogStore.accept(DialogStore.Intent.CloseDialog)

    private fun bindLocationLabel(label: Label) = when (label) {
        is Label.NewLocation -> mapConfigStore.accept(Intent.CheckLocation(label.latLng))
        else -> Unit
    }
}