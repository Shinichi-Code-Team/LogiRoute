package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.result.RouteEvaluationResult
import com.example.logiroute.domain.model.result.ShipmentRouteResult
import com.example.logiroute.domain.repository.RouteRepository

class EvaluateRouteUseCase(
    private val routeRepository: RouteRepository
) {

    operator fun invoke(
        shipmentRoute: ShipmentRouteResult
    ): RouteEvaluationResult {

        val routeSegments = findRouteSegments(shipmentRoute.path)

        return RouteEvaluationResult(
            totalDistanceKm = calculateTotalDistance(routeSegments),
            totalExpectedDelayMin = calculateTotalDelay(routeSegments),
            hopCount = routeSegments.size
        )
    }

    private fun findRouteSegments(
        path: List<Warehouse>
    ): List<Route> {
        val allRoutes = routeRepository.getAllRoutes()
        return path
            .zipWithNext()
            .map { (origin, destination) ->
                allRoutes.firstOrNull { route ->
                    route.origin == origin &&
                            route.destination ==
                            destination
                } ?: throw LogisticsException
                    .RouteSegmentNotFoundException(
                        "Route segment not found: " +
                                "${origin.id} -> " +
                                destination.id
                    )
            }
    }

    private fun calculateTotalDistance(
        routes: List<Route>
    ): Double {
        return routes.sumOf {
            it.distanceKm
        }
    }

    private fun calculateTotalDelay(
        routes: List<Route>
    ): Int {
        return routes.sumOf {
            it.typicalDelayMin
        }
    }
}