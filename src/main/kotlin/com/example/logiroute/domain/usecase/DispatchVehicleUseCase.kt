package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.VehicleAssignment

class DispatchVehicleUseCase {

    operator fun invoke(
        warehouse: Warehouse,
        assignment: VehicleAssignment
    ): List<Package> {

        loadPackagesIntoVehicle(
            assignment
        )

        removePackagesFromWarehouse(
            warehouse = warehouse,
            packages = assignment.packages
        )

        return assignment.packages
    }

    private fun loadPackagesIntoVehicle(
        assignment: VehicleAssignment
    ) {

        assignment.vehicle
            .loadedPackages
            .addAll(
                assignment.packages
            )
    }

    private fun removePackagesFromWarehouse(
        warehouse: Warehouse,
        packages: List<Package>
    ) {

        packages.forEach {
                packageItem ->

            warehouse.removePackage(
                packageItem
            )
        }
    }
}