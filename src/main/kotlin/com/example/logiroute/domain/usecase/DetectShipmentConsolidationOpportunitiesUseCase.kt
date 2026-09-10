package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.model.request.ConsolidationOpportunityRequest

class DetectShipmentConsolidationOpportunitiesUseCase(
    private val packageRepository: PackageRepository,
    private val findOptimalPathUseCase: FindOptimalPathUseCase
) {

    operator fun invoke(warehouse: Warehouse): List<ConsolidationOpportunityRequest> {
        val packages = packageRepository.getAllPackages().filter { it.origin.id == warehouse.id }
        val opportunities = mapPackagesToOpportunities(packages, warehouse)
        return filterSubOpportunities(opportunities)
    }

    private fun mapPackagesToOpportunities(
        packages: List<Package>,
        warehouse: Warehouse
    ): List<ConsolidationOpportunityRequest> {
        return packages.mapNotNull { packageItem ->
            buildOpportunityForPackage(packageItem, warehouse)
        }
    }

    private fun buildOpportunityForPackage(
        mainPackage: Package,
        currentWarehouse: Warehouse
    ): ConsolidationOpportunityRequest? {
        val route = findOptimalPathUseCase(
            source = currentWarehouse,
            destination = mainPackage.destination
        ) ?: return null

        val compatiblePackages = findCompatiblePackages(mainPackage, currentWarehouse, route)

        if (compatiblePackages.isEmpty()) return null

        return ConsolidationOpportunityRequest(
            mainPackage = mainPackage,
            compatiblePackages = compatiblePackages,
            sharedRoute = route
        )
    }

    private fun findCompatiblePackages(
        mainPackage: Package,
        currentWarehouse: Warehouse,
        mainRoute: List<Warehouse>
    ): List<Package> {
        return packageRepository.getAllPackages()
            .filter { candidate -> candidate.origin.id == currentWarehouse.id }
            .filter { candidate -> candidate.id != mainPackage.id }
            .filter { candidate -> isCandidateRouteCompatible(mainRoute, candidate, currentWarehouse) }
    }

    private fun isCandidateRouteCompatible(
        mainRoute: List<Warehouse>,
        candidatePackage: Package,
        currentWarehouse: Warehouse
    ): Boolean {
        val candidateRoute = findOptimalPathUseCase(
            source = currentWarehouse,
            destination = candidatePackage.destination
        ) ?: return false

        return mainRoute.containsAll(candidateRoute)
    }

    private fun filterSubOpportunities(
        opportunities: List<ConsolidationOpportunityRequest>
    ): List<ConsolidationOpportunityRequest> {
        return opportunities.filterNot { opportunity ->
            isSubOpportunity(opportunity, opportunities)
        }
    }

    private fun isSubOpportunity(
        target: ConsolidationOpportunityRequest,
        all: List<ConsolidationOpportunityRequest>
    ): Boolean {
        val targetIds = target.compatiblePackages.map { it.id }
        return all.any { other ->
            other.mainPackage.id != target.mainPackage.id &&
                    other.compatiblePackages.map { it.id }.containsAll(targetIds)
        }
    }
}