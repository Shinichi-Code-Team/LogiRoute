package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle

class ReassignPackagesAfterBreakdownUseCase {

    operator fun invoke(
        currentAssignments: Map<Vehicle, List<Package>>,
        vehicles: List<Vehicle>,
        brokenVehiclePosition: Int
    ): Map<Vehicle, List<Package>> {
        val vehicleSlots =
            VehicleAssignmentRingConfig.POSITIONS.zip(vehicles)

        val brokenVehicle = vehicleSlots
            .firstOrNull { it.first == brokenVehiclePosition }
            ?.second
            ?: return currentAssignments

        val nextVehicle = vehicleSlots
            .firstOrNull { brokenVehiclePosition < it.first }
            ?.second
            ?: vehicleSlots.firstOrNull()?.second
            ?: return currentAssignments

        if (nextVehicle == brokenVehicle) {
            return currentAssignments
        }

        val packagesToReassign =
            currentAssignments[brokenVehicle].orEmpty()

        val assignmentsWithoutBrokenVehicle =
            currentAssignments - brokenVehicle

        val packagesAlreadyAssignedToNextVehicle =
            assignmentsWithoutBrokenVehicle[nextVehicle].orEmpty()

        val updatedNextVehiclePackages =
            packagesAlreadyAssignedToNextVehicle + packagesToReassign

        return assignmentsWithoutBrokenVehicle +
                (nextVehicle to updatedNextVehiclePackages)
    }
}