package com.example.logiroute.domain.logic.packagepricing.basepricing

import com.example.logiroute.domain.model.Priority

interface DispatchStrategy {
    fun calculateTransitCost(
        weight: Double,
        distanceKm: Double
    ): Double

    fun getPriorityMultiplier(
        priority: Priority
    ): Double
}