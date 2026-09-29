package com.example.logiroute.domain.pricingPackage.servicepricing

import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.pricingPackage.basepricing.RoutePricingEngine

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