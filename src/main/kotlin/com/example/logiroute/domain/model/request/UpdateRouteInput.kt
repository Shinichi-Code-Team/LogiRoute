package com.example.logiroute.domain.model.request

data class UpdateRouteInput(
    val originHubId: String? = null,
    val destinationHubId: String? = null,
    val distanceKm: Double? = null,
    val typicalDelayMin: Int? = null
)