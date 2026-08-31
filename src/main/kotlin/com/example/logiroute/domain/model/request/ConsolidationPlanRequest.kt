package com.example.logiroute.com.example.logiroute.domain.model.request

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle

data class ConsolidationPlanRequest(
    val vehicle: Vehicle,
    val selectedPackages: List<Package>,
    val totalWeight: Double,
    val remainingCapacity: Double
)