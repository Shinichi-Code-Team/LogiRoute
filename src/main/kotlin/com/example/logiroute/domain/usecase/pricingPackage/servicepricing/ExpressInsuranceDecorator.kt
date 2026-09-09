package com.example.logiroute.domain.usecase.pricingPackage.servicepricing

const val EXPRESS_INSURANCE_FEE = 30.0

class ExpressInsuranceDecorator(
    wrappedComponent: PackageComponent,
) : PackageDecorator(wrappedComponent) {

    override fun calculateCost(baseCost: Double): Double {
        return super.calculateCost(baseCost) + EXPRESS_INSURANCE_FEE
    }
}