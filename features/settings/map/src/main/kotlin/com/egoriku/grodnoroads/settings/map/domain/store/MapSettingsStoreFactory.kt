package com.egoriku.grodnoroads.settings.map.domain.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.egoriku.grodnoroads.settings.map.domain.component.MapSettingsComponent.MapDialogState.*
import com.egoriku.grodnoroads.settings.map.domain.component.MapSettingsComponent.MapPref.*
import com.egoriku.grodnoroads.settings.map.domain.component.MapSettingsComponent.MapSettings
import com.egoriku.grodnoroads.settings.map.domain.component.MapSettingsComponent.MapSettings.*
import com.egoriku.grodnoroads.settings.map.domain.store.MapSettingsStore.*
import com.egoriku.grodnoroads.shared.appsettings.extension.edit
import com.egoriku.grodnoroads.shared.appsettings.types.map.drivemode.mapZoomInCity
import com.egoriku.grodnoroads.shared.appsettings.types.map.drivemode.mapZoomOutCity
import com.egoriku.grodnoroads.shared.appsettings.types.map.drivemode.updateMapZoomInCity
import com.egoriku.grodnoroads.shared.appsettings.types.map.drivemode.updateMapZoomOutsideCity
import com.egoriku.grodnoroads.shared.appsettings.types.map.location.defaultCity
import com.egoriku.grodnoroads.shared.appsettings.types.map.location.updateDefaultCity
import com.egoriku.grodnoroads.shared.appsettings.types.map.mapinfo.*
import com.egoriku.grodnoroads.shared.appsettings.types.map.mapstyle.googleMapStyle
import com.egoriku.grodnoroads.shared.appsettings.types.map.mapstyle.trafficJamOnMap
import com.egoriku.grodnoroads.shared.appsettings.types.map.mapstyle.updateGoogleMapStyle
import com.egoriku.grodnoroads.shared.appsettings.types.map.mapstyle.updateTrafficJamAppearance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class MapSettingsStoreFactory(
    private val storeFactory: StoreFactory,
    private val dataStore: DataStore<Preferences>
) {

    @OptIn(ExperimentalMviKotlinApi::class)
    fun create(): MapSettingsStore =
        object : MapSettingsStore, Store<Intent, StoreState, Nothing> by storeFactory.create(
            initialState = StoreState(),
            executorFactory = coroutineExecutorFactory(Dispatchers.Main) {
                onAction<Unit> {
                    launch {
                        dispatch(Message.Loading(true))

                        dataStore.data
                            .map { pref ->
                                MapSettings(
                                    mapInfo = MapInfo(
                                        stationaryCameras = StationaryCameras(isShow = pref.isShowStationaryCameras),
                                        mobileCameras = MobileCameras(isShow = pref.isShowMobileCameras),
                                        trafficPolice = TrafficPolice(isShow = pref.isShowTrafficPolice),
                                        roadIncident = RoadIncident(isShow = pref.isShowRoadIncidents),
                                        carCrash = CarCrash(isShow = pref.isShowCarCrash),
                                        trafficJam = TrafficJam(isShow = pref.isShowTrafficJam),
                                        wildAnimals = WildAnimals(isShow = pref.isShowWildAnimals),
                                    ),
                                    mapStyle = MapStyle(
                                        trafficJamOnMap = TrafficJamOnMap(isShow = pref.trafficJamOnMap),
                                        googleMapStyle = GoogleMapStyle(style = pref.googleMapStyle)
                                    ),
                                    locationInfo = LocationInfo(
                                        defaultCity = DefaultCity(current = pref.defaultCity),
                                    ),
                                    driveModeZoom = DriveModeZoom(
                                        mapZoomInCity = MapZoomInCity(current = pref.mapZoomInCity),
                                        mapZoomOutCity = MapZoomOutCity(current = pref.mapZoomOutCity)
                                    )
                                )
                            }
                            .collect {
                                dispatch(Message.NewSettings(it))
                                dispatch(Message.Loading(false))
                            }
                    }
                }
                onIntent<Intent.Modify> { onCheckedChanged ->
                    val preference = onCheckedChanged.preference

                    launch {
                        dataStore.edit {
                            when (preference) {
                                is StationaryCameras -> updateStationaryCameras(preference.isShow)
                                is MobileCameras -> updateMobileCameras(preference.isShow)
                                is TrafficPolice -> updateTrafficPolice(preference.isShow)
                                is RoadIncident -> updateRoadIncidents(preference.isShow)
                                is CarCrash -> updateCarCrash(preference.isShow)
                                is TrafficJam -> updateTrafficJam(preference.isShow)
                                is WildAnimals -> updateWildAnimals(preference.isShow)

                                is TrafficJamOnMap -> updateTrafficJamAppearance(preference.isShow)
                                is GoogleMapStyle -> updateGoogleMapStyle(preference.style.type)

                                is DefaultCity -> updateDefaultCity(preference.current.cityName)

                                is MapZoomInCity -> updateMapZoomInCity(preference.current)
                                is MapZoomOutCity -> updateMapZoomOutsideCity(preference.current)
                            }
                        }
                    }
                }
                onIntent<Intent.OpenDialog> {
                    when (it.preference) {
                        is DefaultCity -> dispatch(
                            Message.NewDialogState(
                                mapDialogState = DefaultLocationDialogState(defaultCity = it.preference)
                            )
                        )

                        is MapZoomInCity -> dispatch(
                            Message.NewDialogState(
                                mapDialogState = MapZoomInCityDialogState(mapZoomInCity = it.preference)
                            )
                        )

                        is MapZoomOutCity -> dispatch(
                            Message.NewDialogState(
                                mapDialogState = MapZoomOutCityDialogState(mapZoomOutCity = it.preference)
                            )
                        )

                        else -> throw UnsupportedOperationException("${it.preference} not supported")
                    }
                }
                onIntent<Intent.CloseDialog> {
                    dispatch(message = Message.NewDialogState(mapDialogState = None))
                }
            },
            bootstrapper = SimpleBootstrapper(Unit),
            reducer = { message: Message ->
                when (message) {
                    is Message.NewSettings -> copy(mapSettings = message.mapSettings)
                    is Message.NewDialogState -> copy(mapDialogState = message.mapDialogState)
                    is Message.Loading -> copy(isLoading = message.isLoading)
                }
            }
        ) {}
}