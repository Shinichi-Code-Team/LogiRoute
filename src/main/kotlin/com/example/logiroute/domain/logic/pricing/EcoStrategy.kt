package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.domain.model.Priority

class EcoStrategy : DispatchStrategy {

    override fun calculateTransitCost(
        weight: Double,
        distanceKm: Double
    ): Double {
        val weightRate = 0.8
        val distanceRate = 0.5

        return (weight * weightRate) +
                (distanceKm * distanceRate)
    }

    override fun getPriorityMultiplier(priority: Priority): Double {
        return when (priority) {
            Priority.URGENT -> 1.5
            Priority.STANDARD -> 1.0
            Priority.LOW -> 0.8
        }
    }
}