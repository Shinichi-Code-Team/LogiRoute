package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.usecase.model.ConsolidationOpportunity
import com.example.logiroute.domain.usecase.model.ConsolidationPlan

class PrioritizeShipmentConsolidationUseCase {

    operator fun invoke(
        opportunity: ConsolidationOpportunity,
        vehicle: Vehicle
    ): ConsolidationPlan {

        val allPackages = getAllPackages(opportunity)

        val prioritizedPackages =
            prioritizePackages(allPackages)

        val (selectedPackages, totalWeight) =
            selectPackagesWithinCapacity(
                packages = prioritizedPackages,
                vehicle = vehicle
            )

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

    private fun prioritizePackages(
        packages: List<Package>
    ): List<Package> {
        return packages.sortedByDescending {
            it.priority
        }
    }

    private fun selectPackagesWithinCapacity(
        packages: List<Package>,
        vehicle: Vehicle
    ): Pair<List<Package>, Double> {

        return packages.fold(
            emptyList<Package>() to 0.0
        ) { (selectedPackages, currentWeight), packageItem ->

            val nextWeight =
                currentWeight + packageItem.weight

            if (nextWeight <= vehicle.maxCapacityKg) {
                (selectedPackages + packageItem) to nextWeight
            } else {
                selectedPackages to currentWeight
            }
        }
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
            remainingCapacity =
                vehicle.maxCapacityKg - totalWeight
        )
    }
}