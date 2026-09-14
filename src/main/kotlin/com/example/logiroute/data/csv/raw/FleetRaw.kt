package com.example.logiroute.data.csv.raw

data class FleetRaw(
    val vehicleIds: List<String>,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)