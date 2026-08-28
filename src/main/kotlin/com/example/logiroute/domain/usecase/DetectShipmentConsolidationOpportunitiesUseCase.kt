package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.model.ConsolidationOpportunity

class DetectShipmentConsolidationOpportunitiesUseCase(
    private val findOptimalPathUseCase: FindOptimalPathUseCase
) {

    operator fun invoke(
        packages: List<Package>
    ): List<ConsolidationOpportunity> {

        val opportunities = packages
            .map { mainPackage ->
                buildOpportunity(
                    mainPackage = mainPackage,
                    packages = packages
                )
            }
            .filter { opportunity ->
                opportunity.compatiblePackages.isNotEmpty()
            }

        return removeSubOpportunities(opportunities)
    }

    private fun buildOpportunity(
        mainPackage: Package,
        packages: List<Package>
    ): ConsolidationOpportunity {

        val sharedRoute = getSharedRoute(mainPackage)

        val compatiblePackages = findCompatiblePackages(
            mainPackage = mainPackage,
            packages = packages,
            sharedRoute = sharedRoute
        )

        return ConsolidationOpportunity(
            mainPackage = mainPackage,
            compatiblePackages = compatiblePackages,
            sharedRoute = sharedRoute
        )
    }

    private fun getSharedRoute(
        mainPackage: Package
    ): List<Warehouse> {

        return findOptimalPathUseCase(
            mainPackage.origin,
            mainPackage.destination
        )
    }

    private fun findCompatiblePackages(
        mainPackage: Package,
        packages: List<Package>,
        sharedRoute: List<Warehouse>
    ): List<Package> {

        return packages.filter { candidatePackage ->
            candidatePackage != mainPackage &&
                    candidatePackage.origin == mainPackage.origin &&
                    candidatePackage.destination in sharedRoute
        }
    }

    private fun removeSubOpportunities(
        opportunities: List<ConsolidationOpportunity>
    ): List<ConsolidationOpportunity> {

        return opportunities.filter { currentOpportunity ->

            val currentPackages =
                getAllPackages(currentOpportunity)

            opportunities.none { otherOpportunity ->

                val otherPackages =
                    getAllPackages(otherOpportunity)

                otherOpportunity != currentOpportunity &&
                        otherPackages.size > currentPackages.size &&
                        otherPackages.containsAll(currentPackages)
            }
        }
    }

    private fun getAllPackages(
        opportunity: ConsolidationOpportunity
    ): List<Package> {

        return listOf(opportunity.mainPackage) +
                opportunity.compatiblePackages
    }
}