// domain/usecase/ReroutePackageUseCase.kt
package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class ReroutePackageUseCase(
    private val packageRepository: PackageRepository,
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(
        packageId: String,
        newDestinationId: String
    ): Package {

        val packageItem = packageRepository.getAllPackages()
            .find { it.id == packageId }
            ?: throw IllegalArgumentException("Package not found: $packageId")


        val newDestination = warehouseRepository.getAllWarehouses()
            .find { it.id == newDestinationId }
            ?: throw IllegalArgumentException("Warehouse not found: $newDestinationId")

        if (packageItem.destination.id == newDestinationId) {
            throw IllegalArgumentException("Package already destined to this warehouse")
        }


        val removed = packageItem.origin.removePackage(packageItem)
        if (!removed) {
            throw IllegalStateException("Failed to remove package from origin warehouse")
        }


        val updatedPackage = packageItem.copy(
            destination = newDestination
        )


        newDestination.addPackage(updatedPackage)

        return updatedPackage
    }
}