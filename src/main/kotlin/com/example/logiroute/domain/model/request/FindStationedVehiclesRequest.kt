package com.example.logiroute.com.example.logiroute.domain.usecase.model.request

data class FindStationedVehiclesRequest(
    val warehouseId: String,
    val minCapacity: Double
)