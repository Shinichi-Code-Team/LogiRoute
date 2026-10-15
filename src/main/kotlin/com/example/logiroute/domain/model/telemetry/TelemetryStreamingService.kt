package com.example.logiroute.domain.service

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.telemetry.VehicleTelemetry
import kotlinx.coroutines.flow.Flow

interface TelemetryStreamingService {
    fun streamFleet(vehicles: List<Vehicle>): Flow<VehicleTelemetry>
}