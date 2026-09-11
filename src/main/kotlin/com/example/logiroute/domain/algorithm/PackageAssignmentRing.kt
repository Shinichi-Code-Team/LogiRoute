package com.example.logiroute.domain.algorithm

import com.example.logiroute.domain.model.Vehicle

class PackageAssignmentRing(
    activeVehicles: List<Vehicle>
) {
    private companion object {
        const val RING_SIZE = 100
        val POSITIONS = listOf(15, 40, 65, 90)
    }

    private val vehiclePositions =
        POSITIONS.zip(activeVehicles)

    fun findVehicle(packageId: String): Vehicle {
        val slot = Math.floorMod(packageId.hashCode(), RING_SIZE)

        return vehiclePositions
            .firstOrNull { slot <= it.first }
            ?.second
            ?: vehiclePositions.first().second
    }

    fun findVehicleAt(position: Int): Vehicle? =
        vehiclePositions
            .firstOrNull { it.first == position }
            ?.second

    fun findNextVehicle(brokenPosition: Int): Vehicle =
        vehiclePositions
            .firstOrNull { brokenPosition < it.first }
            ?.second
            ?: vehiclePositions.first().second
}