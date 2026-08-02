package com.example.logiroute.domain.logic.pricing

import com.example.logiroute.data.dataholder.PriorityRaw


class ExpressStrategy : DispatchStrategy {

    override fun calculateTransitCost(
        packageWeight: Double,
        distanceKm: Double
    ): Double {
        val weightRate = 3.0
        val distanceRate = 2.3

        return (packageWeight * weightRate) +
                (distanceKm * distanceRate)
    }

    override fun getPriorityMultiplier(priority: PriorityRaw): Double {
        return when (priority) {
            PriorityRaw.URGENT -> 1.4
            PriorityRaw.STANDARD -> 1.1
            PriorityRaw.LOW -> 1.0
        }
    }

}