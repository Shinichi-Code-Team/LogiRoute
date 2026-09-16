package com.example.logiroute.data.remote.dto.warehouse

data class WarehouseResponseDto(
    val id: String,
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
)