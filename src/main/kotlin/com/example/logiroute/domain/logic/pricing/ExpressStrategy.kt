package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.domain.model.Priority


class ExpressStrategy : DispatchStrategy {

    override fun calculateTransitCost(
        weight: Double,
        distanceKm: Double
    ): Double {
        val weightRate = 3.0
        val distanceRate = 2.3

        return (weight * weightRate) +
                (distanceKm * distanceRate)
    }

    override fun getPriorityMultiplier(priority: Priority): Double {
        return when (priority) {
            Priority.URGENT -> 1.4
            Priority.STANDARD -> 1.1
            Priority.LOW -> 1.0
        }
    }

}