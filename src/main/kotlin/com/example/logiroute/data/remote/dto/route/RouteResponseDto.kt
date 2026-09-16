package com.example.logiroute.data.remote.dto.route

import kotlinx.serialization.Serializable

@Serializable
data class RouteResponseDto(
    val id: String,
    val originHubId: String,
    val destinationHubId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int
)