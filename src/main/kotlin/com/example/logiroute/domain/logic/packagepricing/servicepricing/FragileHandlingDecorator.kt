package com.example.logiroute.domain.logic.packagepricing.servicepricing

const val FRAGILE_FEE = 15.0

class FragileHandlingDecorator(
    wrappedComponent: PackageComponent,
) : PackageDecorator(wrappedComponent) {

    override fun calculateCost(baseCost: Double): Double {
        return super.calculateCost(baseCost) + FRAGILE_FEE
    }
}