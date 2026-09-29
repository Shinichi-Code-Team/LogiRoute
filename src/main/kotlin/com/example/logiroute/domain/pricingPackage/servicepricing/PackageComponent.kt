package com.example.logiroute.domain.pricingPackage.servicepricing

interface PackageComponent {
    fun calculateCost(baseCost: Double) : Double
}