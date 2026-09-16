package com.example.logiroute.data.remote.dto.vehicle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleResponseDto(
    val id: String,

    @SerialName("current_hub_id")
    val currentHubId: String,

    @SerialName("max_capacity_kg")
    val maxCapacityKg: Double,

    @SerialName("cost_per_km")
    val costPerKm: Double
)