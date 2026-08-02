package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.domain.model.Priority
class RoutePricingEngine(private var activeStrategy: DispatchStrategy) {
    fun switchStrategy(newStrategy: DispatchStrategy) {
        activeStrategy = newStrategy
    }

    fun computeFinalCost(distanceKm: Double, weight: Double, priority: Priority): Double {
        val transitCost = activeStrategy.calculateTransitCost(distanceKm, weight)
        val priorityMultiplier = activeStrategy.getPriorityMultiplier(priority)
        return transitCost * priorityMultiplier
    }

}