package com.example.logiroute.domain.logic.packagepricing.servicepricing

class ColdChainDecorator(
    wrappedComponent: PackageComponent,
    private val coolingFee: Double = 25.0
) : PackageDecorator(wrappedComponent) {

    override fun calculateCost(baseCost: Double): Double {
        return super.calculateCost(baseCost) + coolingFee
    }
}