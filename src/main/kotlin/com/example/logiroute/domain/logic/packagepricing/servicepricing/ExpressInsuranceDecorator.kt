package com.example.logiroute.domain.logic.packagepricing.servicepricing

class ExpressInsuranceDecorator(
    wrappedComponent: PackageComponent,
    private val insurancePercentage: Double = 0.10 // 10% تأمين
) : PackageDecorator(wrappedComponent) {

    override fun calculateCost(baseCost: Double): Double {
        val currentCost = super.calculateCost(baseCost)
        return currentCost + (currentCost * insurancePercentage)
    }
}