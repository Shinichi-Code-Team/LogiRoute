package com.example.logiroute.data.remote.dto.`package`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePackageRequestDto(
    @SerialName("weight")
    val weight: Double? = null,

    @SerialName("origin_hub_id")
    val originHubId: String? = null,

    @SerialName("destination_hub_id")
    val destinationHubId: String? = null,

    @SerialName("priority")
    val priority: PriorityDto? = null
)