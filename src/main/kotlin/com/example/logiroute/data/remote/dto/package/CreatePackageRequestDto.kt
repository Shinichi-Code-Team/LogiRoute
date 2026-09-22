package com.example.logiroute.data.remote.dto.`package`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePackageRequestDto(
    @SerialName("id")
    val id: String,

    @SerialName("weight")
    val weight: Double,

    @SerialName("origin_hub_id")
    val originHubId: String,

    @SerialName("destination_hub_id")
    val destinationHubId: String,

    @SerialName("priority")
    val priority: PriorityDto
)