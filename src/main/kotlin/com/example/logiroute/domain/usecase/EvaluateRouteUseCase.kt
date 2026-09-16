package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException

class EvaluateRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(path: List<Warehouse>): Double {
        if (path.size < 2) return 0.0
        return calculateTotalRouteDistance(path)
    }

    private suspend fun calculateTotalRouteDistance(path: List<Warehouse>): Double {
        return path.zipWithNext().sumOf { (origin, destination) ->
            fetchSegmentDistance(origin, destination)
        }
    }

    private suspend fun fetchSegmentDistance(origin: Warehouse, destination: Warehouse): Double {
        val segment = routeRepository.getAllRoutes().find { route ->
            route.origin.id == origin.id && route.destination.id == destination.id
        } ?: throw LogisticsException.RouteSegmentNotFoundException(
            "Route segment not found between ${origin.name} and ${destination.name}"
        )
        return segment.distanceKm
    }
}