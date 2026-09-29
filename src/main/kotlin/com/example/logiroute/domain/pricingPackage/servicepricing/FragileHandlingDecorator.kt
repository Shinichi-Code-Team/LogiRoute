package com.example.logiroute.domain.pricingPackage.servicepricing


class FragileHandlingDecorator(
    wrappedComponent: PackageComponent,
) : PackageDecorator(wrappedComponent) {
    companion object {
        const val FRAGILE_FEE = 15.0
    }
    override fun calculateCost(baseCost: Double): Double {
        return super.calculateCost(baseCost) + FRAGILE_FEE
    }
}