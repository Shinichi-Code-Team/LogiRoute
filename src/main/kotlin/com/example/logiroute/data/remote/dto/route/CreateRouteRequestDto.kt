package com.example.logiroute.data.remote.dto.route

import kotlinx.serialization.Serializable

@Serializable
data class CreateRouteRequestDto(
    val id: String,
    val originHubId: String,
    val destinationHubId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int
)