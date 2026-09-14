package com.example.logiroute.data.remote.dto.`package`

//import kotlinx.serialization.Serializable

//@Serializable
data class UpdatePackageRequestDto(
    val weight: Double? = null,
    val originHubId: String? = null,
    val destinationHubId: String? = null,
    val priority: PriorityDto? = null
)