package com.egoriku.grodnoroads

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.egoriku.grodnoroads.domain.model.Camera
import com.egoriku.grodnoroads.domain.usecase.CameraUseCase
import com.egoriku.grodnoroads.extension.logD
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel(
    application: Application,
    private val useCase: CameraUseCase
) : AndroidViewModel(application) {

    private val _location = MutableStateFlow(
        UserPosition(
            location = LatLng(0.0, 0.0),
            bearing = 0f
        )
    )
    val location = _location.asStateFlow()

    private val _stationary = MutableStateFlow<List<Camera>>(emptyList())
    val stationary = _stationary.asStateFlow()

    init {
        viewModelScope.launch {
            _stationary.emit(useCase.loadStationary())
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location: Location in locationResult.locations) {
                logD("locationCallback: ${location.latitude}, ${location.longitude}, ${location.bearing}")
                _location.tryEmit(
                    UserPosition(
                        location = LatLng(location.latitude, location.longitude),
                        bearing = location.bearing
                    )
                )
            }
        }
    }

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}