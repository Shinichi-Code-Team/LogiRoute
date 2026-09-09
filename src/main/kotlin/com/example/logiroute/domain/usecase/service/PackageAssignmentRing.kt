package com.example.logiroute.domain.usecase.service

import com.example.logiroute.domain.model.Package
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

    private fun moveBrokenVehiclePackages(
        brokenVehicle: Vehicle,
        brokenVehiclePosition: Int,
        currentAssignments: MutableMap<Vehicle, List<Package>>
    ) {
        val brokenPackages = currentAssignments[brokenVehicle] ?: emptyList()
        val nextVehicle = findNextVehicleClockwise(brokenVehiclePosition)
        currentAssignments[nextVehicle] =
            (currentAssignments[nextVehicle] ?: emptyList()) + brokenPackages
        currentAssignments.remove(brokenVehicle)
    }

    fun assignPackagesToVehicles(packages: List<Package>): Map<Vehicle, List<Package>> {
        val assignments = initializeAssignments()
        for (pkg in packages) {
            val packageSlot = calculatePackageSlot(pkg.id)
            val assignedVehicle = findNextVehicleClockwise(packageSlot)
            assignments[assignedVehicle]?.add(pkg)
        }
        return assignments
    }

    fun reassignPackagesAfterBreakdown(
        currentAssignments: Map<Vehicle, List<Package>>,
        brokenVehiclePosition: Int
    ): Map<Vehicle, List<Package>> {
        val updatedAssignments = currentAssignments.toMutableMap()
        val brokenVehicle = vehiclePositionsMap.remove(brokenVehiclePosition)

        if (brokenVehicle != null) {
            moveBrokenVehiclePackages(brokenVehicle, brokenVehiclePosition, updatedAssignments)
        }

        return updatedAssignments
    }

    fun assertStableAssignments(
        beforeBreakdownAssignments: Map<Vehicle, List<Package>>,
        afterBreakdownAssignments: Map<Vehicle, List<Package>>
    ): Boolean {
        for ((vehicle, packagesBefore) in beforeBreakdownAssignments) {
            if (afterBreakdownAssignments.containsKey(vehicle)) {
                if (packagesBefore != afterBreakdownAssignments[vehicle]) {
                    return false
                }
            }
        }
        return true
    }
}