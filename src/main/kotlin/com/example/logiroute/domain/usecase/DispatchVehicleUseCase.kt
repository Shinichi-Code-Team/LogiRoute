package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse

class DispatchVehicleUseCase {

    operator fun invoke(warehouse: Warehouse, vehicle: Vehicle): List<Package> {
        val availablePackages = warehouse.cargoQueue
        val (packagesToDispatch, _) = availablePackages.fold(
            initial = emptyList<Package>() to 0.0
        ) { (accPackages, currentWeight), pkg ->
            val nextWeight = currentWeight + pkg.weight
            if (nextWeight <= vehicle.maxCapacityKg) {
                (accPackages + pkg) to nextWeight
            } else {
                accPackages to currentWeight
            }
        }
        return packagesToDispatch.also { packages ->
            packages.forEach { pkg ->
                warehouse.removePackage(pkg)
            }
        }
    }
}