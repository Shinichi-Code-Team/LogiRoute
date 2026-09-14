package com.example.logiroute.data.remote.dto.vehicle

data class VehicleResponseDto(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHubId: String
)