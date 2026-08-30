package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.logic.packagepricing.basepricing.RoutePricingEngine
                    import com.example.logiroute.domain.logic.packagepricing.servicepricing.PackageComponent
                    import com.example.logiroute.domain.model.Package

class CalculatePricingUseCase(
private val pricingEngine: RoutePricingEngine
) {
operator fun invoke(
    packageItem: Package,
    distanceKm: Double,
    packageComponent: PackageComponent = packageItem
): Double {
    val baseCost = pricingEngine.computeFinalCost(
        distanceKm = distanceKm,
        weight = packageItem.weight,
        priority = packageItem.priority
    )
    return packageComponent.calculateCost(baseCost)
}
}
