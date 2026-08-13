package com.example.logiroute.domain.logic.packagepricing.servicepricing

class FragileHandlingDecorator(
    wrappedComponent: PackageComponent,
    private val fragileFee: Double = 15.0
) : PackageDecorator(wrappedComponent) {

    override fun calculateCost(baseCost: Double): Double {
        return super.calculateCost(baseCost) + fragileFee
    }
}