package com.example.logiroute.domain.usecase.model

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle

data class ConsolidationPlan(
    val vehicle: Vehicle,
    val selectedPackages: List<Package>,
    val totalWeight: Double,
    val remainingCapacity: Double
)