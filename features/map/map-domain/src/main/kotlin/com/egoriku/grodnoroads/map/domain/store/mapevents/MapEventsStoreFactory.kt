package com.egoriku.grodnoroads.map.domain.store.mapevents

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.egoriku.grodnoroads.analytics.AnalyticsTracker
import com.egoriku.grodnoroads.crashlytics.CrashlyticsTracker
import com.egoriku.grodnoroads.extensions.common.ResultOf.Failure
import com.egoriku.grodnoroads.extensions.common.ResultOf.Success
import com.egoriku.grodnoroads.map.domain.model.MapEvent.*
import com.egoriku.grodnoroads.map.domain.model.Source.App
import com.egoriku.grodnoroads.map.domain.model.report.ReportActionModel
import com.egoriku.grodnoroads.map.domain.repository.MobileCameraRepository
import com.egoriku.grodnoroads.map.domain.repository.ReportsRepository
import com.egoriku.grodnoroads.map.domain.repository.StationaryCameraRepository
import com.egoriku.grodnoroads.map.domain.repository.UserCountRepository
import com.egoriku.grodnoroads.map.domain.store.mapevents.MapEventsStore.*
import com.egoriku.grodnoroads.map.domain.store.mapevents.MapEventsStore.Message.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class MapEventsStoreFactory(
    private val storeFactory: StoreFactory,
    private val mobileCameraRepository: MobileCameraRepository,
    private val stationaryCameraRepository: StationaryCameraRepository,
    private val userCountRepository: UserCountRepository,
    private val reportsRepository: ReportsRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val crashlyticsTracker: CrashlyticsTracker
) {

    @OptIn(ExperimentalMviKotlinApi::class)
    fun create(): MapEventsStore =
        object : MapEventsStore, Store<Intent, State, Nothing> by storeFactory.create(
            initialState = State(),
            executorFactory = coroutineExecutorFactory(Dispatchers.Main) {
                onAction<Unit> {
                    launch {
                        subscribeForStationaryCameras {
                            dispatch(OnStationary(data = it))
                        }
                    }
                    launch {
                        subscribeForMobileCameras {
                            dispatch(OnMobileCamera(data = it))
                        }
                    }
                    launch {
                        subscribeForReports {
                            dispatch(OnNewReports(data = it))
                        }
                    }
                    launch {
                        subscribeForUserCount {
                            dispatch(OnUserCount(data = it))
                        }
                    }
                }
                onIntent<Intent.ReportAction> { data ->
                    launch {
                        val params = data.params

                        reportsRepository.report(
                            ReportActionModel(
                                timestamp = System.currentTimeMillis(),
                                type = params.mapEventType.type,
                                message = params.message,
                                shortMessage = params.shortMessage,
                                latitude = params.latLng.latitude,
                                longitude = params.latLng.longitude,
                                source = App.source
                            )
                        )
                        analyticsTracker.eventReportAction(
                            eventType = params.mapEventType.type,
                            shortMessage = params.shortMessage
                        )
                    }
                }
            },
            bootstrapper = SimpleBootstrapper(Unit),
            reducer = { message: Message ->
                when (message) {
                    is OnStationary -> copy(stationaryCameras = message.data)
                    is OnNewReports -> copy(reports = message.data)
                    is OnMobileCamera -> copy(mobileCamera = message.data)
                    is OnUserCount -> copy(userCount = message.data)
                }
            }
        ) {}

    private suspend fun subscribeForMobileCameras(onLoaded: (List<MobileCamera>) -> Unit) {
        mobileCameraRepository.loadAsFlow().collect { result ->
            when (result) {
                is Success -> onLoaded(result.value)
                is Failure -> crashlyticsTracker.recordException(result.exception)
            }
        }
    }

    private suspend fun subscribeForReports(onLoaded: (List<Reports>) -> Unit) {
        reportsRepository.loadAsFlow().collect { result ->
            when (result) {
                is Success -> onLoaded(result.value)
                is Failure -> crashlyticsTracker.recordException(result.exception)
            }
        }
    }

    private suspend fun subscribeForStationaryCameras(onLoaded: (List<StationaryCamera>) -> Unit) {
        stationaryCameraRepository.loadAsFlow().collect { result ->
            when (result) {
                is Success -> onLoaded(result.value)
                is Failure -> crashlyticsTracker.recordException(result.exception)
            }
        }
    }

    private suspend fun subscribeForUserCount(onLoaded: (Int) -> Unit) {
        userCountRepository.loadAsFlow().collect { result ->
            when (result) {
                is Success -> onLoaded(result.value)
                is Failure -> crashlyticsTracker.recordException(result.exception)
            }
        }
    }
}