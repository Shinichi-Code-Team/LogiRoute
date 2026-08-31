package com.example.logiroute.domain.model.result

data class VehicleUtilization(
    val currentLoadKg: Double,
    val remainingCapacityKg: Double,
    val utilizationPercentage: Double
)