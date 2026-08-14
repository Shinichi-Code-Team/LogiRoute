package com.example.logiroute.domain.logic.packagepricing.servicepricing

import com.example.logiroute.domain.logic.packagepricing.basepricing.RoutePricingEngine
import com.example.logiroute.domain.model.Priority

class DecoratedPackagePricingService(
    private val prcingEngine: RoutePricingEngine
) {
    fun calculatePackageCost(
        packageComponent: PackageComponent,
        distanceKm: Double,
        weight: Double,
        priority: Priority
    ): Double {
        val baseCost = prcingEngine.computeFinalCost(
            distanceKm,
            weight,
            priority
        )
        return packageComponent.calculateCost(baseCost)
    }

}