package com.example.logiroute.domain.service

import com.example.logiroute.domain.model.Vehicle

class PackageAssignmentRing(
    private val activeVehicles: List<Vehicle>
) {
    private val ringSize = 100
    private val vehiclePositionsMap = mutableMapOf<Int, Vehicle>()

    init {
        val predefinedPositions = listOf(15, 40, 65, 90)
        predefinedPositions.zip(activeVehicles).forEach { (position, vehicle) ->
            vehiclePositionsMap[position] = vehicle
        }
    }

    private fun initializeAssignments(): MutableMap<Vehicle, MutableList<Package>> {
        val assignments = mutableMapOf<Vehicle, MutableList<Package>>()
        vehiclePositionsMap.values.forEach { assignments[it] = mutableListOf() }
        return assignments
    }

    private fun calculatePackageSlot(packageId: String): Int {
        return Math.abs(packageId.hashCode() % ringSize)
    }

    private fun findNextVehicleClockwise(slot: Int): Vehicle {
        val sortedPositions = vehiclePositionsMap.keys.sorted()
        for (position in sortedPositions) {
            if (slot <= position) return vehiclePositionsMap.getValue(position)
        }
        return vehiclePositionsMap.getValue(sortedPositions.first())
    }
}