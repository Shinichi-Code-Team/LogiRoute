package com.example.logiroute.domain.pricingPackage.servicepricing


class ColdChainDecorator(
    wrappedComponent: PackageComponent,
) : PackageDecorator(wrappedComponent) {
    companion object {
        const val COLD_CHAIN_MULTIPLIER = 1.15
    }
    override fun calculateCost(baseCost: Double): Double {
        return super.calculateCost(baseCost) * COLD_CHAIN_MULTIPLIER
    }
}