package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.VehicleAssignment

class DispatchVehicleUseCase {

    operator fun invoke(
        warehouse: Warehouse,
        assignment: VehicleAssignment
    ): List<Package> {

        validateVehicleCapacity(assignment)

        loadPackagesIntoVehicle(assignment)

        removePackagesFromWarehouse(
            warehouse = warehouse,
            packages = assignment.packages
        )

        return assignment.packages
    }

    private fun validateVehicleCapacity(
        assignment: VehicleAssignment
    ) {
        val vehicle = assignment.vehicle

        val currentWeight = vehicle.loadedPackages.sumOf {
            it.weight
        }

        val newWeight = assignment.packages.sumOf {
            it.weight
        }

        require(
            currentWeight + newWeight <= vehicle.maxCapacityKg
        ) {
            "Vehicle capacity exceeded"
        }
    }

    private fun loadPackagesIntoVehicle(
        assignment: VehicleAssignment
    ) {
        assignment.vehicle.loadedPackages.addAll(
            assignment.packages
        )
    }

    private fun removePackagesFromWarehouse(
        warehouse: Warehouse,
        packages: List<Package>
    ) {
        packages.forEach { packageItem ->
            warehouse.removePackage(packageItem)
        }
    }
}