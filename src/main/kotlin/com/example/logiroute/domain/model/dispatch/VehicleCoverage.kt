package com.example.logiroute.domain.model.dispatch

data class VehicleCoverage(
    val vehicleId: String,
    val coveredZones: Set<String>
)