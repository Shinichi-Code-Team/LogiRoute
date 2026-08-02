package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.data.dataholder.PriorityRaw

interface DispatchStrategy {
    fun calculateTransitCost(distanceKm: Double, weight: Double): Double
    fun getPriorityMultiplier (priority: PriorityRaw): Double
}