package com.example.logiroute.data.telemetry

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.telemetry.VehicleTelemetry
import com.example.logiroute.domain.service.TelemetryStreamingService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlin.math.cos
import kotlin.math.sin

class SimulatedTelemetryStreamingService(
    private val intervalMillis: Long = DEFAULT_INTERVAL_MILLIS
) : TelemetryStreamingService {

    init {
        require(intervalMillis > 0L) {
            "Telemetry interval must be positive."
        }
    }

    override fun streamFleet(
        vehicles: List<Vehicle>
    ): Flow<VehicleTelemetry> = flow {
        require(vehicles.isNotEmpty()) {
            "At least one vehicle is required."
        }

        generateSequence(0L) { sequence -> sequence + 1L }
            .asFlow()
            .collect { sequence ->
                vehicles
                    .map { vehicle ->
                        telemetryAt(vehicle, sequence)
                    }
                    .forEach { telemetry ->
                        emit(telemetry)
                    }

                delay(intervalMillis)
            }
    }

    private fun telemetryAt(
        vehicle: Vehicle,
        sequence: Long
    ): VehicleTelemetry {
        val vehicleOffset = Math.floorMod(vehicle.id.hashCode(), 100)
        val phase = (sequence + vehicleOffset).toDouble() / 10.0

        val latitude = (
                vehicle.currentHub.latitude + sin(phase) * GPS_OFFSET
                ).coerceIn(-90.0, 90.0)

        val longitude = (
                vehicle.currentHub.longitude + cos(phase) * GPS_OFFSET
                ).coerceIn(-180.0, 180.0)

        val speedKph = (
                (vehicleOffset.toLong() + sequence * SPEED_STEP) % MAX_SPEED
                ).toDouble()

        val fuelPercent = (
                INITIAL_FUEL_PERCENT -
                        sequence.toDouble() * FUEL_DECREASE_PER_UPDATE -
                        vehicleOffset % 10
                ).coerceAtLeast(0.0)

        return VehicleTelemetry(
            vehicleId = vehicle.id,
            latitude = latitude,
            longitude = longitude,
            speedKph = speedKph,
            fuelPercent = fuelPercent,
            sequence = sequence
        )
    }

    private companion object {
        const val DEFAULT_INTERVAL_MILLIS = 1_000L
        const val GPS_OFFSET = 0.01
        const val SPEED_STEP = 7L
        const val MAX_SPEED = 81L
        const val INITIAL_FUEL_PERCENT = 100.0
        const val FUEL_DECREASE_PER_UPDATE = 0.1
    }
}