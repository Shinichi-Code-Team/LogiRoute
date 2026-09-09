package com.example.logiroute.domain.model

import com.example.logiroute.domain.usecase.pricingPackage.servicepricing.PackageComponent

data class Package(
    val id: String,
    val weight: Double,
    val origin: Warehouse,
    val destination: Warehouse,
    val priority: Priority
) : PackageComponent {
    override fun calculateCost(baseCost: Double): Double {
        return baseCost
    }
}