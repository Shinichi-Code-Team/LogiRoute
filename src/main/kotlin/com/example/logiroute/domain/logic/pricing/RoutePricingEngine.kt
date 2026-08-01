package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.data.dataholder.PriorityRaw

class RoutePricingEngine(private var activeStrategy: DispatchStrategy) {
    fun switchStrategy(newStrategy: DispatchStrategy) {
        activeStrategy = newStrategy
    }

    fun computeFinalCost(distanceKm: Double, weight: Double, priority: PriorityRaw): Double {
        val transitCost = activeStrategy.calculateTransitCost(distanceKm, weight)
        val priorityMultiplier = activeStrategy.getPriorityMultiplier(priority)
        return transitCost * priorityMultiplier
    }

}