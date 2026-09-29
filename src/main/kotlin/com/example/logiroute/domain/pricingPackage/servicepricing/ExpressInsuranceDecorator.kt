package com.example.logiroute.domain.pricingPackage.servicepricing

class ExpressInsuranceDecorator(
    wrappedComponent: PackageComponent,
) : PackageDecorator(wrappedComponent) {
    companion object {
        const val EXPRESS_INSURANCE_FEE = 30.0
    }
    override fun calculateCost(baseCost: Double): Double {
        return super.calculateCost(baseCost) + EXPRESS_INSURANCE_FEE
    }
}