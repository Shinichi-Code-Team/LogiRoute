package com.example.logiroute.domain.model.telemetry

data class VehicleTelemetry(
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double,
    val speedKph: Double,
    val fuelPercent: Double,
    val sequence: Long
) {
    init {
        require(latitude.isFinite() && latitude in -90.0..90.0)
        require(longitude.isFinite() && longitude in -180.0..180.0)
        require(speedKph.isFinite() && speedKph >= 0.0)
        require(fuelPercent.isFinite() && fuelPercent in 0.0..100.0)
        require(sequence >= 0L)
    }
}