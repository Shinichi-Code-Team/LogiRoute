package com.example.logiroute.domain.model

data class Route(
    val routeId: String,
    val origin: Warehouse,
    val destination: Warehouse,
    val distanceKm: Double,
    val typicalDelayMin: Int
)
{
    init {
        require(routeId.isNotBlank()) { "Route id must not be blank" }
        require(distanceKm >= 0) { "Route distance cannot be negative" }
        require(typicalDelayMin >= 0) { "Route delay cannot be negative" }
    }
}