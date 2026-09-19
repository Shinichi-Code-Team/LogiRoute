package com.example.logiroute.domain.model.request

data class UpdateVehicleInput(
    val maxCapacityKg: Double? = null,
    val costPerKm: Double? = null,
    val currentHubId: String? = null
)