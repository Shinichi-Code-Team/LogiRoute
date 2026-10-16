package com.example.logiroute.domain.model.telemetry

data class DelayNotification(
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double,
    val speedKph: Double,
    val message: String,
    val sequence: Long
)