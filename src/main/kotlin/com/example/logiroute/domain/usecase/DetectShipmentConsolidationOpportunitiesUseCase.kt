package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.com.example.logiroute.domain.model.request.ConsolidationOpportunityRequest

class DetectShipmentConsolidationOpportunitiesUseCase(
    private val findOptimalPathUseCase: FindOptimalPathUseCase
) {

    operator fun invoke(
        packages: List<Package>
    ): List<ConsolidationOpportunityRequest> {

        val packagesByOrigin = packages.groupBy { it.origin }
        val routeCache =
            mutableMapOf<Pair<Warehouse, Warehouse>, List<Warehouse>>()

        val opportunities = packages
            .map { mainPackage ->

                val sameOriginPackages =
                    packagesByOrigin[mainPackage.origin].orEmpty()

                buildOpportunity(
                    mainPackage = mainPackage,
                    packages = sameOriginPackages,
                    routeCache = routeCache
                )
            }
            .filter { opportunity ->
                opportunity.compatiblePackages.isNotEmpty()
            }

        return removeSubOpportunities(opportunities)
    }

    private fun buildOpportunity(
        mainPackage: Package,
        packages: List<Package>,
        routeCache: MutableMap<
                Pair<Warehouse, Warehouse>,
                List<Warehouse>
                >
    ): ConsolidationOpportunityRequest {

        val sharedRoute =
            getSharedRoute(
                mainPackage = mainPackage,
                routeCache = routeCache
            )

        val compatiblePackages =
            findCompatiblePackages(
                mainPackage = mainPackage,
                packages = packages,
                sharedRoute = sharedRoute
            )

        return ConsolidationOpportunityRequest(
            mainPackage = mainPackage,
            compatiblePackages = compatiblePackages,
            sharedRoute = sharedRoute
        )
    }

    private fun getSharedRoute(
        mainPackage: Package,
        routeCache: MutableMap<
                Pair<Warehouse, Warehouse>,
                List<Warehouse>
                >
    ): List<Warehouse> {

        val routeKey =
            mainPackage.origin to mainPackage.destination

        return routeCache.getOrPut(routeKey) {

            findOptimalPathUseCase(
                mainPackage.origin,
                mainPackage.destination
            )
        }
    }

    private fun findCompatiblePackages(
        mainPackage: Package,
        packages: List<Package>,
        sharedRoute: List<Warehouse>
    ): List<Package> {

        val routeWarehouses = sharedRoute.toSet()

        return packages.filter { candidatePackage ->

            candidatePackage != mainPackage &&
                    candidatePackage.destination in routeWarehouses
        }
    }

    private fun removeSubOpportunities(
        opportunities: List<ConsolidationOpportunityRequest>
    ): List<ConsolidationOpportunityRequest> {

        val distinctOpportunities =
            opportunities.distinctBy { opportunity ->

                getAllPackages(opportunity)
                    .map { it.id }
                    .sorted()
            }

        val packageSets =
            distinctOpportunities.associateWith { opportunity ->

                getAllPackages(opportunity).toSet()
            }

        return distinctOpportunities.filter { currentOpportunity ->

            val currentPackages =
                packageSets.getValue(currentOpportunity)

            distinctOpportunities.none { otherOpportunity ->

                val otherPackages =
                    packageSets.getValue(otherOpportunity)

                otherOpportunity != currentOpportunity &&
                        otherPackages.size > currentPackages.size &&
                        otherPackages.containsAll(currentPackages)
            }
        }
    }

    private fun getAllPackages(
        opportunity: ConsolidationOpportunityRequest
    ): List<Package> {

        return listOf(opportunity.mainPackage) +
                opportunity.compatiblePackages
    }
}