package com.example.logiroute.domain.usecase.service

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import kotlin.math.abs

private const val RING_SIZE = 100

class PackageAssignmentRing2 {
    private val vehicleSlots = mutableListOf<Int>()
    private val vehicleBySlots = mutableMapOf<Int, Vehicle>()
    private val assignments = mutableMapOf<Int, MutableList<Package>>()

    fun addVehicle(vehicleSlot: Int, vehicle: Vehicle) {
        vehicleSlots.add(vehicleSlot)
        vehicleBySlots[vehicleSlot] = vehicle
        assignments[vehicleSlot] = mutableListOf()
    }

    private fun calculatePackageSlot(packageId: String): Int {
        return abs(packageId.hashCode() % RING_SIZE)
    }

    private fun findClockWiseVehicleSlot(packageSlot: Int): Int {
        for (vehicleSlot in vehicleSlots) {
            if (vehicleSlot >= packageSlot) {
                return vehicleSlot
            }
        }
        return vehicleSlots.first()
    }

    private fun addPackageToVehicle(vehicleSlot: Int, packageItem: Package) {
        val vehiclePackages = assignments.getValue(vehicleSlot)
        vehiclePackages.add(packageItem)
    }

    private fun assignPackage(packageItem: Package): Boolean {
        val packageSlot = calculatePackageSlot(packageItem.id)
        val firstVehicleSlot = findClockWiseVehicleSlot(packageSlot)
        val firstVehicleIndex = vehicleSlots.indexOf(firstVehicleSlot)
        for (offset in vehicleSlots.indices) {
            val vehicleIndex = (firstVehicleIndex + offset) % vehicleSlots.size
            val vehicleSlot = vehicleSlots[vehicleIndex]
            if (canCarryPackage(vehicleSlot, packageItem)) {
                addPackageToVehicle(vehicleSlot, packageItem)
                return true
            }
        }
        return false
    }

    fun assignPackages(packages: List<Package>) {
        for (packageItem in packages) {
            assignPackage(packageItem)
        }
    }

    fun removeVehicle(vehicleSlot: Int): List<Package> {
        val brokenVehiclePackages = assignments.getValue(vehicleSlot).toList()
        vehicleSlots.remove(vehicleSlot)
        vehicleBySlots.remove(vehicleSlot)
        assignments.remove(vehicleSlot)
        return brokenVehiclePackages
    }

    fun reroutePackages(packages: List<Package>): List<Package> {
        val unassignedPackages = mutableListOf<Package>()
        for (packageItem in packages) {
            val wasAssigned = assignPackage(packageItem)
            if (!wasAssigned) {
                unassignedPackages.add(packageItem)
            }
        }
        return unassignedPackages
    }

    fun copyAssignments(): Map<Int, List<Package>> {
        val copiedAssignments = mutableMapOf<Int, List<Package>>()
        for ((vehicleSlot, packages) in assignments) {
            copiedAssignments[vehicleSlot] =
                packages.toList()
        }

        return copiedAssignments
    }

    private fun calculateCurrentLoad(vehicleSlot: Int): Double {
        val vehiclePackages = assignments.getValue(vehicleSlot)
        var currentLoad = 0.0
        for (packageItem in vehiclePackages) {
            currentLoad += packageItem.weight
        }
        return currentLoad
    }

    private fun canCarryPackage(vehicleSlot: Int, packageItem: Package): Boolean {
        val vehicle = vehicleBySlots.getValue(vehicleSlot)
        val currentLoad = calculateCurrentLoad(vehicleSlot)
        return currentLoad + packageItem.weight <= vehicle.maxCapacityKg
    }

    fun getAssignments(): Map<Int, List<Package>> {
        return assignments
    }
}