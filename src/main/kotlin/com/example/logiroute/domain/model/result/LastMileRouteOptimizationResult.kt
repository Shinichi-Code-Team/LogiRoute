package com.example.logiroute.domain.model.result

import com.example.logiroute.domain.model.LastMileWaypoint

data class LastMileRouteOptimizationResult(
    val route: List<LastMileWaypoint>,
    val distanceBeforeKm: Double,
    val distanceAfterKm: Double
) {
    val distanceSavedKm: Double
        get() = (distanceBeforeKm - distanceAfterKm).coerceAtLeast(0.0)

    val distanceSavedPercent: Double
        get() = distanceBeforeKm
            .takeIf { distance -> distance > 0.0 }
            ?.let { distance ->
                ((distance - distanceAfterKm) / distance * 100.0)
                    .coerceIn(0.0, 100.0)
            }
            ?: 0.0
    val estimatedFuelSavedPercent: Double
        get() = distanceSavedPercent
}