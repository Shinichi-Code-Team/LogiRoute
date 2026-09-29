package com.example.logiroute.domain.usecase.pricingPackage.servicepricing

const val COLD_CHAIN_MULTIPLIER =  1.15

class ColdChainDecorator(
    wrappedComponent: PackageComponent,
) : PackageDecorator(wrappedComponent) {

    override fun calculateCost(baseCost: Double): Double {
        return super.calculateCost(baseCost) * COLD_CHAIN_MULTIPLIER
    }
}