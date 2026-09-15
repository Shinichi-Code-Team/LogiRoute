package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.algorithm.sorting.PackageSelectionSort
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.request.ConsolidationOpportunityRequest

class PrioritizeShipmentConsolidationUseCase(
    private val packageSelectionSort: PackageSelectionSort
) {

    operator fun invoke(
        opportunity: ConsolidationOpportunityRequest
    ): List<Package> {

        val allPackages =
            getAllPackages(opportunity)

        return packageSelectionSort
            .sortPackagesByPriorityConsideringWeight(
                allPackages
            )
    }

    private fun getAllPackages(
        opportunity: ConsolidationOpportunityRequest
    ): List<Package> {

        return listOf(opportunity.mainPackage) +
                opportunity.compatiblePackages
    }
}