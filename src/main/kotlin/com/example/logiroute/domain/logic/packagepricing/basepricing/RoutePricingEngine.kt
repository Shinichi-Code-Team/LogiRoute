package com.example.logiroute.domain.logic.packagepricing.basepricing

import com.example.logiroute.domain.model.Priority
class RoutePricingEngine(private var activeStrategy: DispatchStrategy) {
    fun switchStrategy(newStrategy: DispatchStrategy) {
        activeStrategy = newStrategy
    }

    fun computeFinalCost(distanceKm: Double, weight: Double, priority: Priority): Double {
        val transitCost = activeStrategy.calculateTransitCost( weight , distanceKm )
        val priorityMultiplier = activeStrategy.getPriorityMultiplier(priority)
        return transitCost * priorityMultiplier
    }

}