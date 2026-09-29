package com.example.logiroute.domain.usecase.pricingPackage.servicepricing

abstract class PackageDecorator(
    protected val wrappedComponent: PackageComponent
) : PackageComponent {

    override fun calculateCost(baseCost: Double): Double {
        return wrappedComponent.calculateCost(baseCost)
    }
}