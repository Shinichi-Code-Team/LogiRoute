package com.example.logiroute.presentation.cli

import com.example.logiroute.domain.model.LastMileWaypoint
import com.example.logiroute.domain.model.request.OptimizeLastMileRouteRequest
import com.example.logiroute.domain.usecase.OptimizeLastMileRouteUseCase
import com.example.logiroute.domain.usecase.crud.warehouse.ReadWarehouseUseCase

class LastMileCli(
    private val readWarehouseUseCase: ReadWarehouseUseCase,
    private val optimizeLastMileRouteUseCase: OptimizeLastMileRouteUseCase
) {
    suspend fun run() {
        while (true) {
            printMenu()
            when (readCliInput("Choose a last-mile command:") ?: "0") {
                "1" -> safelyRunCliCommand { optimizeRoute() }
                "0" -> return
                else -> println("Unknown option. Choose one of the listed commands.")
            }
        }
    }

    private fun printMenu() {
        println()
        println("=== Last-Mile CLI ===")
        println("1. Optimize a route with 2-Opt")
        println("0. Back to main menu")
    }

    private suspend fun optimizeRoute() {
        val originId = readCliInput("Origin warehouse ID:") ?: return
        val origin = readWarehouseUseCase(originId)
            .getOrElse { error ->
                println("Could not load origin warehouse: ${error.message}")
                return
            }
            ?: run {
                println("Origin warehouse was not found.")
                return
            }

        val deliveryStops = readDeliveryStops()
            ?: run {
                println("Route optimization cancelled. Enter valid coordinates.")
                return
            }

        val request = OptimizeLastMileRouteRequest(
            origin = LastMileWaypoint(
                id = origin.id,
                latitude = origin.latitude,
                longitude = origin.longitude
            ),
            deliveryStops = deliveryStops
        )
        val result = optimizeLastMileRouteUseCase(request)

        println("Optimized route:")
        println(result.route.joinToString(" -> ") { waypoint -> waypoint.id })
        println("Distance before: ${result.distanceBeforeKm} km")
        println("Distance after: ${result.distanceAfterKm} km")
        println("Distance saved: ${result.distanceSavedKm} km")
        println("Driving distance saved: ${result.distanceSavedPercent}%")
        println("Estimated fuel saved: ${result.estimatedFuelSavedPercent}%")
    }

    private suspend fun readDeliveryStops(
        stopNumber: Int = 1,
        collectedStops: List<LastMileWaypoint> = emptyList()
    ): List<LastMileWaypoint>? =
        when {
            stopNumber > 15 -> collectedStops
            else -> readDeliveryStop(stopNumber)?.let { stop ->
                readDeliveryStops(stopNumber + 1, collectedStops + stop)
            }
        }

    private suspend fun readDeliveryStop(stopNumber: Int): LastMileWaypoint? {
        val latitude = readFiniteCliDouble("Stop $stopNumber latitude:")
            ?.takeIf { value -> value in -90.0..90.0 }
            ?: run {
                println("Latitude must be between -90 and 90.")
                return null
            }

        val longitude = readFiniteCliDouble("Stop $stopNumber longitude:")
            ?.takeIf { value -> value in -180.0..180.0 }
            ?: run {
                println("Longitude must be between -180 and 180.")
                return null
            }

        return LastMileWaypoint(
            id = "STOP-${stopNumber.toString().padStart(2, '0')}",
            latitude = latitude,
            longitude = longitude
        )
    }
}
