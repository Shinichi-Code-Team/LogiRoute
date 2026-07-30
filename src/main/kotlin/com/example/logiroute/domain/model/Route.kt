package com.example.logiroute.domain.model

data class Route(
    val routeId: String,
    val origin: Warehouse,
    val destination: Warehouse,
    val distanceKm: Double,
    val typicalDelayMin: Int
)