package com.example.logiroute.dataholder

data class RouteRow(
    val routeId: String,
    val originHubId: String,
    val destinationHubId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int
)