package com.example.logiroute.domain.logic.packagepricing.servicepricing

interface PackageComponent {
    fun calculateCost(baseCost: Double) : Double
}