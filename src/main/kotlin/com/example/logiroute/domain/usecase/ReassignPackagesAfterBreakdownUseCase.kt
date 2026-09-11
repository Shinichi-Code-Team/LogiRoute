package com.example.logiroute.com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.algorithm.PackageAssignmentRing

class ReassignPackagesAfterBreakdownUseCase {

        operator fun invoke(
            currentAssignments: Map<Vehicle, List<Package>>,
            vehicles: List<Vehicle>,
            brokenVehiclePosition: Int
        ): Map<Vehicle, List<Package>> {

            val ring = PackageAssignmentRing(vehicles)

            val brokenVehicle =
                ring.findVehicleAt(brokenVehiclePosition)
                    ?: return currentAssignments

            val nextVehicle =
                ring.findNextVehicle(brokenVehiclePosition)

            val brokenPackages =
                currentAssignments[brokenVehicle].orEmpty()

            return currentAssignments
                .filterKeys { it != brokenVehicle }
                .mapValues { (vehicle, packages) ->
                    if (vehicle == nextVehicle) {
                        packages + brokenPackages
                    } else {
                        packages
                    }
                }
        }
    }

