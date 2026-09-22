package com.example.logiroute.data.remote.dto.route

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRouteRequestDto(
    @SerialName("id")
    val id: String,

    @SerialName("origin_hub_id")
    val originHubId: String,

    @SerialName("destination_hub_id")
    val destinationHubId: String,

    @SerialName("distance_km")
    val distanceKm: Double,

    @SerialName("typical_delay_min")
    val typicalDelayMin: Int
)