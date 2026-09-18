package com.example.logiroute.domain.model.request

data class UpdateWarehouseInput(
    val name: String? = null,
    val regionalZone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)