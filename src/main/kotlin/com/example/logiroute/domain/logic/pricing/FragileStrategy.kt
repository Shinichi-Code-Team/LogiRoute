package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.domain.model.Priority

class FragileStrategy : DispatchStrategy {

    override fun calculateTransitCost(
        weight: Double,
        distanceKm: Double
    ): Double {
        val weightRate = 1.0
        val distanceRate = 1.3
        val safetyFee = 8.0

        return (weight * weightRate) +
                (distanceKm * distanceRate) +
                safetyFee
    }

    override fun getPriorityMultiplier(priority: Priority): Double {
        return when (priority) {
            Priority.URGENT -> 1.5
            Priority.STANDARD -> 1.0
            Priority.LOW -> 0.8
        }
    }

}