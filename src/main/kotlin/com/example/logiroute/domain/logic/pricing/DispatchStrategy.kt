package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.data.dataholder.PriorityRaw

interface DispatchStrategy {
    fun calculateTransitCost(packageWeight: Double, distanceKm: Double): Double
    fun getPriorityMultiplier (priority: PriorityRaw): Double
}