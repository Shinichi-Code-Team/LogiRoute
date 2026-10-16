package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.telemetry.DelayNotification
import com.example.logiroute.domain.model.telemetry.VehicleTelemetry
import com.example.logiroute.domain.service.TelemetryStreamingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class TrackActiveFleetUseCase(
    private val telemetryStreamingService: TelemetryStreamingService,
    private val delaySpeedThresholdKph: Double = DEFAULT_DELAY_THRESHOLD_KPH
) {
    init {
        require(
            delaySpeedThresholdKph.isFinite() &&
                    delaySpeedThresholdKph >= 0.0
        ) {
            "Delay speed threshold must be finite and non-negative."
        }
    }

    operator fun invoke(
        vehicles: List<Vehicle>
    ): Flow<DelayNotification> =
        telemetryStreamingService
            .streamFleet(vehicles)
            .filter { telemetry ->
                telemetry.speedKph < delaySpeedThresholdKph
            }
            .map(::toDelayNotification)

    private fun toDelayNotification(
        telemetry: VehicleTelemetry
    ) = DelayNotification(
        vehicleId = telemetry.vehicleId,
        latitude = telemetry.latitude,
        longitude = telemetry.longitude,
        speedKph = telemetry.speedKph,
        message = "Vehicle speed is below the delay threshold.",
        sequence = telemetry.sequence
    )

    private companion object {
        const val DEFAULT_DELAY_THRESHOLD_KPH = 15.0
    }
}