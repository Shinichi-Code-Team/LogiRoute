package com.example.logiroute.data.remote.dto.warehouse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWarehouseRequestDto(
    val name: String? = null,
    @SerialName("regional_zone")
    val regionalZone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)