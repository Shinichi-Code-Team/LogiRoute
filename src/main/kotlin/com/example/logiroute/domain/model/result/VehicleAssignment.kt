package com.example.logiroute.domain.model.result

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle

data class VehicleAssignment(
    val vehicle: Vehicle,
    val packages: List<Package>,
    val totalWeightKg: Double,
    val remainingCapacityKg: Double
)