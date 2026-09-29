package com.example.logiroute.domain.model.request
data class FindStationedVehiclesRequest(
    val warehouseId: String,
    val minCapacity: Double
)