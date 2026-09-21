package com.example.logiroute.domain.model.dispatch

data class DispatchResult(
    val selectedVehicleIds: List<String>,
    val coveredZones: Set<String>,
    val uncoveredZones: Set<String>
)