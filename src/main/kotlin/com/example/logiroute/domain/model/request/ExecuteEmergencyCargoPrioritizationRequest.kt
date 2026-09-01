package com.example.logiroute.domain.model.request

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle

data class ExecuteEmergencyCargoPrioritizationRequest(
    val opportunity: RescueOpportunity
)

data class EmergencyDispatchPlan(
    val vehicle: Vehicle,
    val loadedUrgentPackages: List<Package>,
    val offloadedLowPriorityPackages: List<Package>,
    val totalWeight: Double,
    val remainingCapacity: Double
)