package com.egoriku.grodnoroads.map.data.repository

import com.egoriku.grodnoroads.extensions.awaitSingleValueEventListener
import com.egoriku.grodnoroads.extensions.common.ResultOf
import com.egoriku.grodnoroads.extensions.common.ResultOf.Failure
import com.egoriku.grodnoroads.extensions.common.ResultOf.Success
import com.egoriku.grodnoroads.map.data.dto.StationaryDTO
import com.egoriku.grodnoroads.map.domain.model.MapEvent.StationaryCamera
import com.egoriku.grodnoroads.map.domain.repository.StationaryCameraRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class StationaryCameraRepositoryImpl(
    private val databaseReference: DatabaseReference
) : StationaryCameraRepository {

    override fun loadAsFlow(): Flow<ResultOf<List<StationaryCamera>>> {
        return databaseReference
            .child("stationary_camera")
            .awaitSingleValueEventListener<StationaryDTO>()
            .map { resultOf ->
                when (resultOf) {
                    is Failure -> Failure(resultOf.exception)
                    is Success -> Success(resultOf.value.map { data ->
                        StationaryCamera(
                            message = data.message,
                            speed = data.speed,
                            position = LatLng(data.latitude, data.longitude)
                        )
                    })
                }
            }
            .flowOn(Dispatchers.IO)
    }
}