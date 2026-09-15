package com.example.logiroute.data.remote.dto.vehicle

data class UpdateVehicleRequestDto(
    val maxCapacityKg: Double? = null,
    val costPerKm: Double? = null,
    val currentHubId: String? = null
)