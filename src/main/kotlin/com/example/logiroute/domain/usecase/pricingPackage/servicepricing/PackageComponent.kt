package com.example.logiroute.domain.usecase.pricingPackage.servicepricing

interface PackageComponent {
    fun calculateCost(baseCost: Double) : Double
}