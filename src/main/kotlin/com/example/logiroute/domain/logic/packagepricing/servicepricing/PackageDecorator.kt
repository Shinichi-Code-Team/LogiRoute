package com.example.logiroute.domain.logic.packagepricing.servicepricing

abstract class PackageDecorator(
    protected val wrappedComponent: PackageComponent
) : PackageComponent {

    override fun calculateCost(baseCost: Double): Double {
        return wrappedComponent.calculateCost(baseCost)
    }
}