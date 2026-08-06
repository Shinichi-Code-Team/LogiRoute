package com.example.logiroute.domain.model

data class Route(
    val id: String,
    val origin: Warehouse,
    val destination: Warehouse,
    val distanceKm: Double,
    val typicalDelayMin: Int
)