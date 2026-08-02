package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.data.dataholder.PriorityRaw

class FragileStrategy : DispatchStrategy {

    override fun calculateTransitCost(
        packageWeight: Double,
        distanceKm: Double
    ): Double {
        val weightRate = 1.0
        val distanceRate = 1.3
        val safetyFee = 8.0

        return (packageWeight * weightRate) +
                (distanceKm * distanceRate) +
                safetyFee
    }

    override fun getPriorityMultiplier(priority: PriorityRaw): Double {
        return when (priority) {
            PriorityRaw.URGENT -> 1.5
            PriorityRaw.STANDARD -> 1.0
            PriorityRaw.LOW -> 0.8
        }
    }

}