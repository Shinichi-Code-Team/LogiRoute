package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.usecase.model.ConsolidationOpportunity
import com.example.logiroute.domain.usecase.model.ConsolidationPlan

class OptimizeShipmentConsolidationUseCase {

    operator fun invoke(
        opportunity: ConsolidationOpportunity,
        vehicle: Vehicle
    ): ConsolidationPlan {

        val allPackages = getAllPackages(opportunity)

        val selectedPackages = selectPackagesWithinCapacity(
            packages = allPackages,
            vehicle = vehicle
        )

        val totalWeight = calculateTotalWeight(selectedPackages)

        return buildConsolidationPlan(
            vehicle = vehicle,
            selectedPackages = selectedPackages,
            totalWeight = totalWeight
        )
    }

    private fun getAllPackages(
        opportunity: ConsolidationOpportunity
    ): List<Package> {
        return listOf(opportunity.mainPackage) +
                opportunity.compatiblePackages
    }

    private fun selectPackagesWithinCapacity(
        packages: List<Package>,
        vehicle: Vehicle
    ): List<Package> {

        return packages.fold(emptyList()) { selectedPackages, packageItem ->

            val currentWeight =
                calculateTotalWeight(selectedPackages)

            if (
                currentWeight + packageItem.weight
                <= vehicle.maxCapacityKg
            ) {
                selectedPackages + packageItem
            } else {
                selectedPackages
            }
        }
    }

    private fun calculateTotalWeight(
        packages: List<Package>
    ): Double {
        return packages.sumOf { it.weight }
    }

    private fun buildConsolidationPlan(
        vehicle: Vehicle,
        selectedPackages: List<Package>,
        totalWeight: Double
    ): ConsolidationPlan {

        return ConsolidationPlan(
            vehicle = vehicle,
            selectedPackages = selectedPackages,
            totalWeight = totalWeight,
            remainingCapacity = vehicle.maxCapacityKg - totalWeight
        )
    }
}