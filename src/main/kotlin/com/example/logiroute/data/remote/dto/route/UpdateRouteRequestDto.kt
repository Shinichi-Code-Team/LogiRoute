package com.example.logiroute.data.remote.dto.route

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRouteRequestDto(
    val originHubId: String? = null,
    val destinationHubId: String? = null,
    val distanceKm: Double? = null,
    val typicalDelayMin: Int? = null
)