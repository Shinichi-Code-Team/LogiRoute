package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.request.ConsolidationOpportunityRequest

class PrioritizeShipmentConsolidationUseCase(
    private val sortPackages: SortPackagesByPriorityAndWeightUseCase
) {
    operator fun invoke(
        opportunity: ConsolidationOpportunityRequest
    ): List<Package> {
        val packages =
            listOf(opportunity.mainPackage) + opportunity.compatiblePackages

        return sortPackages(packages)
    }
}

    private fun getAllPackages(
        opportunity: ConsolidationOpportunityRequest
    ): List<Package> {

        return listOf(opportunity.mainPackage) +
                opportunity.compatiblePackages
    }