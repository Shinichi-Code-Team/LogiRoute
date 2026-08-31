package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.model.request.ConsolidationOpportunityRequest
import com.example.logiroute.domain.logic.algorithm.sorting.PackageSelectionSort
import com.example.logiroute.domain.model.Package

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