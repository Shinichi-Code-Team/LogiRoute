package com.example.logiroute.domain.model

data class Vehicle (
    val vehicleId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
    )
{
    init {
        require(vehicleId.isNotBlank()) { "Vehicle id must not be blank" }
        require(maxCapacityKg > 0) { "Vehicle capacity must be positive" }
        require(costPerKm >= 0) { "Vehicle cost per km cannot be negative" }
    }
}