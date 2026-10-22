package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.LastMileWaypoint
import com.example.logiroute.domain.model.request.OptimizeLastMileRouteRequest
import com.example.logiroute.domain.model.result.LastMileRouteOptimizationResult
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class OptimizeLastMileRouteUseCase {

    operator fun invoke(
        request: OptimizeLastMileRouteRequest
    ): LastMileRouteOptimizationResult {
        require(request.deliveryStops.size == REQUIRED_DELIVERY_STOPS) {
            "Exactly $REQUIRED_DELIVERY_STOPS delivery stops are required."
        }

        require(request.deliveryStops.none { stop ->
            stop.id == request.origin.id
        }) {
            "A delivery stop must not use the origin warehouse ID."
        }

        val initialRoute = listOf(request.origin) +
                request.deliveryStops +
                request.origin

        val optimizedRoute = optimizeUntilLocalMinimum(initialRoute)

        return LastMileRouteOptimizationResult(
            route = optimizedRoute,
            distanceBeforeKm = calculateRouteDistance(initialRoute),
            distanceAfterKm = calculateRouteDistance(optimizedRoute)
        )
    }

    private fun optimizeUntilLocalMinimum(
        initialRoute: List<LastMileWaypoint>
    ): List<LastMileWaypoint> =
        generateSequence(initialRoute, ::findFirstImprovingSwap).last()

    private fun findFirstImprovingSwap(
        route: List<LastMileWaypoint>
    ): List<LastMileWaypoint>? =
        (1 until route.lastIndex - 1)
            .asSequence()
            .flatMap { startIndex ->
                (startIndex + 1 until route.lastIndex)
                    .asSequence()
                    .map { endIndex -> startIndex to endIndex }
            }
            .firstNotNullOfOrNull { (startIndex, endIndex) ->
                reverseIfShorter(
                    route = route,
                    startIndex = startIndex,
                    endIndex = endIndex
                )
            }

    private fun reverseIfShorter(
        route: List<LastMileWaypoint>,
        startIndex: Int,
        endIndex: Int
    ): List<LastMileWaypoint>? {
        val oldDistance =
            distanceBetween(route[startIndex - 1], route[startIndex]) +
                    distanceBetween(route[endIndex], route[endIndex + 1])

        val newDistance =
            distanceBetween(route[startIndex - 1], route[endIndex]) +
                    distanceBetween(route[startIndex], route[endIndex + 1])

        return newDistance
            .takeIf { candidateDistance ->
                candidateDistance + IMPROVEMENT_EPSILON_KM < oldDistance
            }
            ?.let {
                route.take(startIndex) +
                        route.subList(startIndex, endIndex + 1).asReversed() +
                        route.drop(endIndex + 1)
            }
    }

    private fun calculateRouteDistance(
        route: List<LastMileWaypoint>
    ): Double =
        route
            .zipWithNext { first, second ->
                distanceBetween(first, second)
            }
            .sum()

    private fun distanceBetween(
        first: LastMileWaypoint,
        second: LastMileWaypoint
    ): Double {
        val firstLatitude = first.latitude.toRadians()
        val secondLatitude = second.latitude.toRadians()
        val latitudeDifference = secondLatitude - firstLatitude
        val longitudeDifference =
            (second.longitude - first.longitude).toRadians()

        val haversine =
            sin(latitudeDifference / 2.0) *
                    sin(latitudeDifference / 2.0) +
                    cos(firstLatitude) *
                    cos(secondLatitude) *
                    sin(longitudeDifference / 2.0) *
                    sin(longitudeDifference / 2.0)

        return 2.0 * EARTH_RADIUS_KM *
                asin(sqrt(haversine.coerceIn(0.0, 1.0)))
    }

    private fun Double.toRadians(): Double =
        this * PI / 180.0

    private companion object {
        const val REQUIRED_DELIVERY_STOPS = 15
        const val EARTH_RADIUS_KM = 6371.0088
        const val IMPROVEMENT_EPSILON_KM = 0.000000001
    }
}