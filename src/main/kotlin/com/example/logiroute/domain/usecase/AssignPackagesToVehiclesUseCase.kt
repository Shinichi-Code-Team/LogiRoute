package com.example.logiroute.com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.algorithm.PackageAssignmentRing

class AssignPackagesToVehiclesUseCase {

    operator fun invoke(
        packages: List<Package>,
        vehicles: List<Vehicle>
    ): Map<Vehicle, List<Package>> {

        val ring = PackageAssignmentRing(vehicles)

        val assignments = packages.groupBy { packageItem ->
            ring.findVehicle(packageItem.id)
        }

        return vehicles.associateWith { vehicle ->
            assignments[vehicle].orEmpty()
        }
    }
}