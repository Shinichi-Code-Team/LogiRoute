package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.data.dataholder.PriorityRaw

class EcoStrategy : DispatchStrategy {

    override fun calculateTransitCost(
        packageWeight: Double,
        distanceKm: Double
    ): Double {
        val weightRate = 0.8
        val distanceRate = 0.5

        return (packageWeight * weightRate) +
                (distanceKm * distanceRate)
    }

    override fun getPriorityMultiplier(priority: PriorityRaw): Double {
        return when (priority) {
            PriorityRaw.URGENT -> 1.5
            PriorityRaw.STANDARD -> 1.0
            PriorityRaw.LOW -> 0.8
        }
    }

}