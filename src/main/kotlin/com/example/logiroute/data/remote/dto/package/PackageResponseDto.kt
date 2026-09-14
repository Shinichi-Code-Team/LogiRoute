package com.example.logiroute.data.remote.dto.`package`

//import kotlinx.serialization.Serializable

//@Serializable
data class PackageResponseDto(
    val id: String,
    val weight: Double,
    val originHubId: String,
    val destinationHubId: String,
    val priority: PriorityDto
)